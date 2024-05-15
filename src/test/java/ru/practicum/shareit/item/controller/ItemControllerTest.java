package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService service;

    @Autowired
    MockMvc mockMvc;

    User owner = new User(1L, "name", "email@email.ru");

    ItemDto itemDto = new ItemDto(1L, "name", "description", true, owner, null);

    @Test
    void create() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", "description", true, null);
        when(service.create(1L, itemCreateDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        verify(service).create(1L, itemCreateDto);
    }

    @Test
    void createWithInvalidName() throws Exception {
        ItemDto itemDtoTest = new ItemDto(1L, "", "description", true, owner, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidDescription() throws Exception {
        ItemDto itemDtoTest = new ItemDto(1L, "name", "", null, owner, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidAvailable() throws Exception {
        ItemDto itemDtoTest = new ItemDto(1L, "name", "description", null, owner, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("newName", "newDescription", true);
        ItemDto itemDtoTest = new ItemDto(1L, "newName", "newDescription", true, owner, null);
        when(service.update(1L, 1L, itemUpdateDto)).thenReturn(itemDtoTest);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoTest.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoTest.getDescription())));

        verify(service).update(1L, 1L, itemUpdateDto);
    }

    @Test
    void getOwnerItems() throws Exception {
        ItemBookingDto itemBookingDtoTest = new ItemBookingDto(1L, "name", "desc", true, owner, null, null, Collections.emptyList());
        when(service.getOwnerItems(1L, 0, 10)).thenReturn(List.of(itemBookingDtoTest));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemBookingDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        ItemBookingDto itemBookingDtoTest = new ItemBookingDto(1L, "name", "desc", true, owner, null, null, Collections.emptyList());
        when(service.getById(1L, 1L)).thenReturn(itemBookingDtoTest);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemBookingDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        when(service.search(1L, "desc", 0, 10)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("text", "desc"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto("text");
        Item itemTest = new Item(1L, "name", "desc", true, owner, null);
        User author = new User(1L, "name", "email@email.ru");
        CommentDto commentDtoTest = new CommentDto(1L, "text", LocalDateTime.now(), itemTest, author, "name");
        when(service.addComment(1L, 1L, commentCreateDto)).thenReturn(commentDtoTest);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(commentDtoTest.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoTest.getAuthorName())));

        verify(service).addComment(1L, 1L, commentCreateDto);
    }

    @Test
    void addCommentWithInvalidText() throws Exception {
        Item itemTest = new Item(1L, "name", "desc", true, owner, null);
        User author = new User(1L, "name", "email@email.ru");
        CommentDto commentDtoTest = new CommentDto(1L, "", LocalDateTime.now(), itemTest, author, "name");

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}