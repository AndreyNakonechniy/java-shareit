package ru.practicum.shareit.user.storage;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class UserStorageImpl implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        checkUser(userId);
        return users.get(userId);
    }

    @Override
    public User createUser(UserDto userDto) {
        User user = UserMapper.userFromDto(userDto);
        user.setId(id);
        checkEmail(user);
        users.put(id, user);
        id++;
        return user;
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        checkUser(userId);
        User userToCheck = UserMapper.userFromDto(userDto);
        userToCheck.setId(userId);
        checkEmail(userToCheck);
        User user = users.get(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());

        }
        users.put(userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        checkUser(userId);
        users.remove(userId);
    }

    private void checkEmail(User user) {
        boolean isNotUnique = users.values().stream().anyMatch(thisUser -> thisUser.getEmail().equals(user.getEmail())
                && !thisUser.getId().equals(user.getId()));

        if (isNotUnique) {
            throw new DuplicateEmailException("Пользователь с таким email уже есть");
        }
    }

    private void checkUser(Long userId){
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
    }
}
