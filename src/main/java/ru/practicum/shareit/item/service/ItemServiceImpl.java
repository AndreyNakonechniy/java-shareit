package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private UserStorage userStorage;
    private ItemStorage itemStorage;

    public List<Item> getOwnerItems(Long userId) {
        userStorage.checkUser(userId);
        return itemStorage.getOwnerItems(userId);
    }

    public Item getById(Long userId, Long itemId) {
        userStorage.checkUser(userId);
        return itemStorage.getById(itemId);
    }

    public Item create(Long userId, ItemDto itemDto) {
        userStorage.checkUser(userId);
        return itemStorage.create(userId, itemDto);
    }

    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        userStorage.checkUser(userId);
        return itemStorage.update(userId, itemId, itemDto);
    }

    public List<Item> search(Long userId, String text) {
        userStorage.checkUser(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text);
    }

}
