package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper = new BookingMapper();

    @Override
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });
        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Нет вещи с таким id");
        });
        bookingValidation(user, item, bookingCreateDto);
        Booking booking = bookingRepository.save(mapper.bookingCreate(bookingCreateDto, user, item));
        return mapper.toBookingDto(booking);
    }

    @Override
    public BookingDto update(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Нет такой брони");
        });
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем этой вещи");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Статус должен быть Waiting");
        }
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        bookingRepository.save(booking);
        return mapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Нет такой брони");
        });
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Операция может быть выполнена только владельцем вещи или автором бронирования");
        }
        return mapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAll(Long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });
        if (from < 0 || size <= 0) {
            throw new ValidationException("Некоректные данные");
        }
        switch (state) {
            case "ALL":
                return convertToBookingDto(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, PageRequest.of(from / size, size)));
            case "CURRENT":
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from / size, size)));
            case "PAST":
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(from / size, size)));
            case "FUTURE":
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(from / size, size)));
            case "WAITING":
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(userId, LocalDateTime.now(), BookingStatus.valueOf(state), PageRequest.of(from / size, size)));
            case "REJECTED":
                return convertToBookingDto(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state), PageRequest.of(from / size, size)));
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getAllOwner(Long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });
        if (from < 0 || size <= 0) {
            throw new ValidationException("Некоректные данные");
        }
        switch (state) {
            case "ALL":
                return convertToBookingDto(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, PageRequest.of(from / size, size)));
            case "CURRENT":
                return convertToBookingDto(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from / size, size)));
            case "PAST":
                return convertToBookingDto(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(from / size, size)));
            case "FUTURE":
                return convertToBookingDto(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(from / size, size)));
            case "WAITING":
                return convertToBookingDto(bookingRepository.findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(userId, LocalDateTime.now(), BookingStatus.valueOf(state), PageRequest.of(from / size, size)));
            case "REJECTED":
                return convertToBookingDto(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.valueOf(state), PageRequest.of(from / size, size)));
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    private void bookingValidation(User user, Item item, BookingCreateDto bookingCreateDto) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart()) || bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd())
                || bookingCreateDto.getStart().equals(bookingCreateDto.getEnd())) {
            throw new ValidationException("Некорректная дата начала или окончания");
        }
    }

    private List<BookingDto> convertToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(mapper.toBookingDto(booking));
        }
        return bookingsDto;
    }
}
