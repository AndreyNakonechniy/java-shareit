package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapper {
    public Item toItem(ItemCreateDto itemCreateDto, User user) {
        return new Item(itemCreateDto.getName(), itemCreateDto.getDescription(), itemCreateDto.getAvailable(), user);
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner());
    }

    public ItemBookingDto toItemBookingDto(Item item) {
        return new ItemBookingDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner());
    }
}
