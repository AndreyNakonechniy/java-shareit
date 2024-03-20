package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserCreateDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.List;

public interface UserService {
    public List<UserDto> getAllUsers();

    public UserDto getUserById(Long id);

    public UserDto createUser(UserCreateDto userCreateDto);

    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto);

    public void deleteUser(Long id);
}
