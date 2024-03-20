package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {

    private Map<Long, ItemDto> items = new HashMap<>();
    private Map<Long, List<ItemDto>> userItems = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        return userItems.get(userId);
    }

    @Override
    public ItemDto getById(Long itemId) {
        itemCheck(itemId);
        return items.get(itemId);
    }

    @Override
    public ItemDto create(ItemDto itemDto) {
        itemDto.setId(id);
        items.put(id, itemDto);
        id++;
        Long userId = itemDto.getOwner().getId();
        List<ItemDto> itemsToMap;
        if (!userItems.containsKey(userId)) {
            itemsToMap = new ArrayList<>();
        } else {
            itemsToMap = userItems.get(userId);
        }
        itemsToMap.add(itemDto);
        userItems.put(userId, itemsToMap);

        return itemDto;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {

        ItemDto itemDto = items.get(itemId);

        if (!itemDto.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У юзера нет этой вещи");
        }
        if (itemUpdateDto.getName() != null) {
            itemDto.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            itemDto.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            itemDto.setAvailable(itemUpdateDto.getAvailable());
        }

        items.put(itemId, itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> search(String text) {
        return items.values().stream().filter(ItemDto::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    public void itemCheck(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Нет такой вещи");
        }
    }
}
