package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class ItemService {

    private UserStorage userStorage;
    private ItemStorage itemStorage;

    public List<Item> getOwnerItems(Long userId) {
        userStorage.getUserById(userId);
        return itemStorage.getOwnerItems(userId);
    }

    public Item getItemById(Long userId, Long itemId) {
        userStorage.getUserById(userId);
        return itemStorage.getItemById(itemId);
    }

    public Item createItem(Long userId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        return itemStorage.createItem(userId, itemDto);
    }

    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    public List<Item> searchItem(Long userId, String text) {
        userStorage.getUserById(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItem(text);
    }

}
