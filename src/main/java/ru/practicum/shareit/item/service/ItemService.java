package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    public ItemDto create(Long userId, ItemCreateDto itemCreateDto);

    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    public List<ItemBookingDto> getOwnerItems(Long userId, int from, int size);

    public ItemBookingDto getById(Long userId, Long itemId);

    public List<ItemDto> search(Long userId, String text, int from, int size);

    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);
}
