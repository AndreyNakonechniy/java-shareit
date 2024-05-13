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
        Item item = new Item(itemCreateDto.getName(), itemCreateDto.getDescription(), itemCreateDto.getAvailable(), user);
        if (itemCreateDto.getRequestId() != null) {
            item.setRequestId(itemCreateDto.getRequestId());
        }
        return item;
    }


    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner());
        if (item.getRequestId() != null) {
            itemDto.setRequestId(item.getRequestId());
        }
        return itemDto;
    }

    public ItemBookingDto toItemBookingDto(Item item) {
        return new ItemBookingDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getOwner());
    }
}
