package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
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

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        userStorage.checkUser(id);
        return userStorage.getById(id);
    }

    public User create(UserCreateDto userCreateDto) {
        UserDto userDto = mapper.userCreateDto(userCreateDto);
        User user = mapper.toUser(userDto);
        return userStorage.create(user);
    }

    public User update(Long id, UserUpdateDto userUpdateDto) {
        userStorage.checkUser(id);
        UserDto userDto = mapper.userUpdate(userUpdateDto);
        userDto.setId(id);
        User user = mapper.toUser(userDto);

        return userStorage.update(user);
    }

    public void delete(Long id) {
        userStorage.delete(id);
    }

}
