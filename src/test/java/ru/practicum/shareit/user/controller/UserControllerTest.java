package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    UserDto userDto = new UserDto(1L, "name", "email@email.com");

    @Test
    void getAll() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void getById() throws Exception {
        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("name", "email@email.com");
        when(userService.create(userCreateDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService).create(userCreateDto);
    }

    @Test
    void createWithInvalidName() throws Exception {
        UserDto userDtoTest = new UserDto(1L, "", "email@email.ru");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidEmail() throws Exception {
        UserDto userDtoTest = new UserDto(1L, "name", "emailemail.ru");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto("newName", "new@email.com");
        UserDto userDtoTest = new UserDto(1L, "newName", "new@email.com");
        when(userService.update(1L, userUpdateDto)).thenReturn(userDtoTest);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDtoTest.getName())))
                .andExpect(jsonPath("$.email", is(userDtoTest.getEmail())));

        verify(userService).update(1L, userUpdateDto);
    }

    @Test
    void deleteTest() throws Exception {

        mockMvc.perform(delete("/0users/{userId}", 1L))
                .andExpect(status().isNoContent());
        verify(userService).delete(1L);
    }
}