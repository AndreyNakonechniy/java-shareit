package ru.practicum.shareit.user.storage;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.*;


@Component
public class UserStorageImpl implements UserStorage {

    private Map<Long, UserDto> users = new HashMap<>();
    private Map<String, Long> emails = new HashMap<>();
    private Long id = 1L;

    private UserMapper mapper;

    public UserStorageImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public UserDto getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setId(id);

        checkEmail(userDto);
        emails.put(userDto.getEmail(), userDto.getId());

        users.put(id, userDto);
        id++;

        return userDto;
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {

        UserDto userToCheck = mapper.userUpdate(userUpdateDto);
        userToCheck.setId(userId);
        checkEmail(userToCheck);

        UserDto userDto = getUserById(userId);
        emails.remove(userDto.getEmail());

        if (userUpdateDto.getName() != null) {
            userDto.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.getEmail() != null) {
            userDto.setEmail(userUpdateDto.getEmail());
            emails.put(userDto.getEmail(), userDto.getId());
        }

        users.put(userDto.getId(), userDto);
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        checkUser(userId);
        UserDto userDto = users.get(userId);
        emails.remove(userDto.getEmail());
        users.remove(userId);
    }

    public void checkEmail(UserDto userDto) {
        if (emails.containsKey(userDto.getEmail())) {
            Long userId = emails.get(userDto.getEmail());
            UserDto userToCheck = users.get(userId);
            if (userToCheck.getEmail().equals(userDto.getEmail()) && !userToCheck.getId().equals(userDto.getId())) {
                throw new DuplicateEmailException("Пользователь с таким email уже есть");
            }
        }
    }

    public void checkUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
    }
}
