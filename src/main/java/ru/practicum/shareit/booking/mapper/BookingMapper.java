package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public Booking bookingCreate(BookingCreateDto bookingCreateDto, User user, Item item) {
        return new Booking(bookingCreateDto.getStart(), bookingCreateDto.getEnd(), item, user, BookingStatus.WAITING);
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus());
    }

    public BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getBooker().getId(), booking.getStatus());
    }
}
