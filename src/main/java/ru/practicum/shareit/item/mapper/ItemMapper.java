package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemMapper {
    public ItemDto itemFromDto(ItemCreateDto itemCreateDto) {
        return new ItemDto(itemCreateDto.getName(), itemCreateDto.getDescription(), itemCreateDto.getAvailable());
    }
}
