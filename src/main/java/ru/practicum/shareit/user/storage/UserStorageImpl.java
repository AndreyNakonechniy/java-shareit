package ru.practicum.shareit.user.storage;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;


@Component
public class UserStorageImpl implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private Map<String, Long> emails = new HashMap<>();
    private Long id = 1L;

    private UserMapper mapper;

    public UserStorageImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        user.setId(id);

        checkEmail(user);
        emails.put(user.getEmail(), user.getId());

        users.put(id, user);
        id++;

        return user;
    }

    @Override
    public User update(User user) {

        checkEmail(user);

        User userUpdate = getById(user.getId());
        emails.remove(userUpdate.getEmail());

        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userUpdate.setEmail(user.getEmail());
            emails.put(userUpdate.getEmail(), userUpdate.getId());
        }

        users.put(userUpdate.getId(), userUpdate);
        return userUpdate;
    }

    @Override
    public void delete(Long userId) {
        checkUser(userId);
        User user = users.get(userId);
        emails.remove(user.getEmail());
        users.remove(userId);
    }

    private void checkEmail(User user) {
        if (emails.containsKey(user.getEmail())) {
            Long userId = emails.get(user.getEmail());
            User userToCheck = users.get(userId);
            if (userToCheck.getEmail().equals(user.getEmail()) && !userToCheck.getId().equals(user.getId())) {
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
