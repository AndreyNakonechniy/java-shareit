package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(userHeader) Long userId, @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Post запрос на создание бронирования");
        return bookingService.create(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader(userHeader) Long userId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        log.info("Patch запрос на обновление статуса бронирования с id: {}", bookingId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBookingById(@RequestHeader(userHeader) Long userId, @PathVariable Long bookingId) {
        log.info("Get запрос на получение бронирования с id: {}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAll(@RequestHeader(userHeader) Long userId, @RequestParam(defaultValue = "ALL") String state,
                                   @RequestParam(defaultValue = "0") @Min(0) int from,
                                   @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Get запрос на получение списка всех бронирований");
        return bookingService.getAll(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwner(@RequestHeader(userHeader) Long userId, @RequestParam(defaultValue = "ALL") String state,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Get запрос на получени списка всех бронирований пользователя с id: {}", userId);
        return bookingService.getAllOwner(userId, state, from, size);
    }
}
