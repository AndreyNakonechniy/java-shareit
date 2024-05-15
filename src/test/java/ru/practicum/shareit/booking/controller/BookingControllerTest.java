package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService service;

    @Autowired
    MockMvc mockMvc;

    User owner = new User(1L, "name", "email@email.ru");
    User booker = new User(2L, "booker", "booker@email.ru");
    Item item = new Item(1L, "name", "desc", true, owner, null);
    BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.WAITING);

    @Test
    void create() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12));
        when(service.create(2L, bookingCreateDto)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));

        verify(service).create(2L, bookingCreateDto);
    }

    @Test
    void createWithInvalidItemId() throws Exception {
        BookingDto bookingDtoTest = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(12), null, booker, BookingStatus.WAITING);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void createWithInvalidStartTime() throws Exception {
        BookingDto bookingDtoTest = new BookingDto(1L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(12), item, booker, BookingStatus.WAITING);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidEndTime() throws Exception {
        BookingDto bookingDtoTest = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(12), item, booker, BookingStatus.WAITING);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus() throws Exception {
        BookingDto bookingDtoTest = new BookingDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(12), item, booker, BookingStatus.APPROVED);
        when(service.update(1L, 1L, true)).thenReturn(bookingDtoTest);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("approved", String.valueOf(true)))
                .andExpect(status().isOk());
        verify(service).update(1L, 1L, true);
    }

    @Test
    void getBookingById() throws Exception {
        when(service.getBookingById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        when(service.getAll(1L, "ALL", 0, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("state", "ALL")
                        .queryParam("from", String.valueOf(0))
                        .queryParam("size", String.valueOf(10)))
                .andExpect(status().isOk());

    }

    @Test
    void getAllOwner() throws Exception {
        when(service.getAllOwner(1L, "ALL", 0, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("state", "ALL")
                        .queryParam("from", String.valueOf(0))
                        .queryParam("size", String.valueOf(10)))
                .andExpect(status().isOk());
    }
}