package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    public List<Item> getOwnerItems(Long userId);

    public Item getItemById(Long itemId);

    public Item createItem(Long userId, ItemDto itemDto);

    public Item updateItem(Long userId, Long itemId, ItemDto itemDto);

    public List<Item> searchItem(String text);
}
