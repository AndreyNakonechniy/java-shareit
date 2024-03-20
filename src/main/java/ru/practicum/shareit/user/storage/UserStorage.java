package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.List;

public interface UserStorage {

    public List<UserDto> getAllUsers();

    public UserDto getUserById(Long id);

    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto);

    public void deleteUser(Long id);

    public void checkEmail(UserDto userDto);

    public void checkUser(Long userId);
}
