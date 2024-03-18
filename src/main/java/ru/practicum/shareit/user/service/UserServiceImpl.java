package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User createUser(UserDto userDto) {
        return userStorage.createUser(userDto);
    }

    public User updateUser(Long id, UserDto userDto) {
        return userStorage.updateUser(id, userDto);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

}
