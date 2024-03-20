package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public List<ItemDto> getOwnerItems(Long userId);

    public ItemDto getById(Long userId, Long itemId);

    public ItemDto create(Long userId, ItemCreateDto itemCreateDto);

    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    public List<ItemDto> search(Long userId, String text);
}
