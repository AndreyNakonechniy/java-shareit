package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserStorage {

    public List<User> getAllUsers();

    public User getUserById(Long id);

    public User createUser(UserDto userDto);

    public User updateUser(Long id, UserDto userDto);

    public void deleteUser(Long id);
}
