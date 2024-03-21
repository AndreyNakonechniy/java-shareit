package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    public List<User> getAll();

    public User getById(Long id);

    public User create(User user);

    public User update(User user);

    public void delete(Long id);

    public void checkUser(Long userId);
}
