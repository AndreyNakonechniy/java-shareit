package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserCreateDto;
import ru.practicum.shareit.user.model.UserUpdateDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;
    private UserMapper mapper;

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public UserDto getUserById(Long id) {
        userStorage.checkUser(id);
        return userStorage.getUserById(id);
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        UserDto userDto = mapper.userCreateDto(userCreateDto);
        return userStorage.createUser(userDto);
    }

    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        userStorage.checkUser(id);

        return userStorage.updateUser(id, userUpdateDto);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

}
