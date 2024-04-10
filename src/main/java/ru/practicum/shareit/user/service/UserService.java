package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    public List<UserDto> getAll();

    public UserDto getById(Long id);

    public UserDto create(UserCreateDto userCreateDto);

    public UserDto update(Long id, UserUpdateDto userUpdateDto);

    public void delete(Long id);
}
