package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserCreateDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.List;

public interface UserService {
    public List<User> getAll();

    public User getById(Long id);

    public User create(UserCreateDto userCreateDto);

    public User update(Long id, UserUpdateDto userUpdateDto);

    public void delete(Long id);
}
