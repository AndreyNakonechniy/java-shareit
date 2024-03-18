package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private final ItemMapper mapper;

    public ItemStorageImpl(ItemMapper mapper) {
        this.mapper = mapper;
    }

    private Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<Item> getOwnerItems(Long userId) {
        return items.values().stream().filter(item -> item.getOwnerId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Item getById(Long itemId) {
        itemCheck(itemId);
        return items.get(itemId);
    }

    @Override
    public Item create(Long userId, ItemDto itemDto) {
        Item item = mapper.itemFromDto(itemDto);
        item.setId(id);
        item.setOwnerId(userId);
        items.put(id, item);
        id++;
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        itemCheck(itemId);

        Item item = items.get(itemId);
        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException("У юзера нет этой вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream().filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    private void itemCheck(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Нет такой вещи");
        }
    }
}
