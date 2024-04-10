package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    public List<ItemBookingDto> getOwnerItems(Long userId);

    public ItemBookingDto getById(Long userId, Long itemId);

    public ItemDto create(Long userId, ItemCreateDto itemCreateDto);

    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    public List<ItemDto> search(Long userId, String text);

    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
