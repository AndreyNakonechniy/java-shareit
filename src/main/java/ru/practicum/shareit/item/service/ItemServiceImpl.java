package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;
    private CommentMapper commentMapper;

    public List<ItemBookingDto> getOwnerItems(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<ItemBookingDto> itemBookingsDto = new ArrayList<>();


        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStart(item.getId());
            ItemBookingDto itemBookingDto = itemMapper.toItemBookingDto(item);
            List<CommentDto> commentsDto = commentRepository.findAllByItemId(item.getId()).stream().map(comment -> commentMapper.toCommentDto(comment)).collect(Collectors.toList());
            itemBookingDto.setComments(commentsDto);
            if (itemBookingDto.getOwner().getId().equals(userId)) {
                itemBookingDto.setLastBooking(lastBooking(bookings));
                itemBookingDto.setNextBooking(nextBooking(bookings));
            } else {
                itemBookingDto.setLastBooking(null);
                itemBookingDto.setNextBooking(null);
            }
            itemBookingsDto.add(itemBookingDto);
        }

        return itemBookingsDto;
    }

    public ItemBookingDto getById(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Нет такой вещи");
        });
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStart(itemId);
        ItemBookingDto itemBookingDto = itemMapper.toItemBookingDto(item);
        List<CommentDto> commentsDto = commentRepository.findAllByItemId(itemId).stream().map(comment -> commentMapper.toCommentDto(comment)).collect(Collectors.toList());
        itemBookingDto.setComments(commentsDto);
        if (itemBookingDto.getOwner().getId().equals(userId)) {
            itemBookingDto.setLastBooking(lastBooking(bookings));
            itemBookingDto.setNextBooking(nextBooking(bookings));
        } else {
            itemBookingDto.setNextBooking(null);
            itemBookingDto.setLastBooking(null);
        }

        return itemBookingDto;
    }

    public ItemDto create(Long userId, ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });
        Item item = itemRepository.save(itemMapper.toItem(itemCreateDto, user));
        return itemMapper.toItemDto(item);
    }

    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Нет такой вещи");
        });

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У юзера нет этой вещи");
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    public List<ItemDto> search(Long userId, String text) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });

        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.search(text);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Нет такой вещи");
        });
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });
        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Владелец вещи не может оставить комментарий");
        }
        List<Booking> userBookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
        if (userBookings.isEmpty()) {
            throw new ValidationException("Некорректные данные");
        }
        userBookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> {
                    throw new NotFoundException("Этот пользователь не брал в аренду эту вещь");
                });
        Comment comment = commentMapper.toComment(commentCreateDto, item, user, LocalDateTime.now());
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    private BookingItemDto lastBooking(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && !booking.getStatus().equals(BookingStatus.REJECTED))
                .reduce((booking1, booking2) -> booking1.getId() > booking2.getId() ? booking1 : booking2)
                .map(booking -> bookingMapper.toBookingItemDto(booking))
                .orElse(null);
    }

    private BookingItemDto nextBooking(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) && !booking.getStatus().equals(BookingStatus.REJECTED))
                .findFirst()
                .map(booking -> bookingMapper.toBookingItemDto(booking))
                .orElse(null);
    }
}
