package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {

    private Map<Long, Item> items = new HashMap<>();
    private Map<Long, List<Item>> userItems = new HashMap<>();
    private Long id = 1L;

    @Override
    public List<Item> getOwnerItems(Long userId) {
        return userItems.get(userId);
    }

    @Override
    public Item getById(Long itemId) {
        itemCheck(itemId);
        return items.get(itemId);
    }

    @Override
    public Item create(Item item) {
        item.setId(id);
        items.put(id, item);
        id++;
        Long userId = item.getOwner().getId();
        List<Item> itemsToMap;
        if (!userItems.containsKey(userId)) {
            itemsToMap = new ArrayList<>();
        } else {
            itemsToMap = userItems.get(userId);
        }
        itemsToMap.add(item);
        userItems.put(userId, itemsToMap);

        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream().filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toList());
    }

    public void itemCheck(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Нет такой вещи");
        }
    }
}
