package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    public List<Item> getOwnerItems(Long userId);

    public Item getById(Long userId, Long itemId);

    public Item create(Long userId, ItemCreateDto itemCreateDto);

    public Item update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    public List<Item> search(Long userId, String text);
}
