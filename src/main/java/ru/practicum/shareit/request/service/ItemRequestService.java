package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    public ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto);

    public List<ItemRequestDto> getUserRequests(Long userId, int from, int size);

    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size);

    public ItemRequestDto getById(Long userId, Long requestId);
}
