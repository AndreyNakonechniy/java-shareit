package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    public List<ItemDto> getOwnerItems(Long userId);

    public ItemDto getById(Long itemId);

    public ItemDto create(ItemDto itemDto);

    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    public List<ItemDto> search(String text);

    public void itemCheck(Long itemId);
}
