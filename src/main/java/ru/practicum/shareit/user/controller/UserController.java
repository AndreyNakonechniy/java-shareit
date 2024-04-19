package ru.practicum.shareit.user.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Get запрос на получение всех пользователей");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Get запрос на получение пользователя с id: {}", userId);
        return userService.getById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Post запрос на добавление пользователя");
        return userService.create(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Patch запрос на изменение пользователя с id: {}", userId);
        return userService.update(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Delete запрос на удаление пользователя с id: {}", userId);
        userService.delete(userId);
    }
}
