package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto);

    public BookingDto update(Long userId, Long bookingId, boolean approved);

    public BookingDto getBookingById(Long userId, Long bookingId);

    public List<BookingDto> getAll(Long userId, String state, int from, int size);

    public List<BookingDto> getAllOwner(Long userId, String state, int from, int size);
}
