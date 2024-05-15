package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class RequestMapper {

    public ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto, User user, LocalDateTime time) {
        return new ItemRequest(itemRequestCreateDto.getDescription(), user, time);
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequester(), itemRequest.getCreated(), itemRequest.getItems());
    }
}
