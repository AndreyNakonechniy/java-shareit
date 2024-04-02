package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    public List<Item> getOwnerItems(Long userId);

    public Item getById(Long itemId);

    public Item create(Item item);

    public Item update(Item item);

    public List<Item> search(String text);

    public void itemCheck(Long itemId);
}
