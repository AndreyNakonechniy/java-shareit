package ru.practicum.shareit.user.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserCreateDto;
import ru.practicum.shareit.user.model.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserCreateDto userCreateDto, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        return userService.createUser(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        userService.deleteUser(userId);
    }
}
