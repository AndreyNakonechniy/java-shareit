package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private UserStorage userStorage;
    private ItemStorage itemStorage;
    private ItemMapper mapper;

    public List<Item> getOwnerItems(Long userId) {
        userStorage.checkUser(userId);
        return itemStorage.getOwnerItems(userId);
    }

    public Item getById(Long userId, Long itemId) {
        userStorage.checkUser(userId);
        return itemStorage.getById(itemId);
    }

    public Item create(Long userId, ItemCreateDto itemCreateDto) {
        userStorage.checkUser(userId);
        User user = userStorage.getById(userId);
        ItemDto itemDto = mapper.itemFromDto(itemCreateDto);
        itemDto.setOwner(user);
        Item item = mapper.toItem(itemDto);
        return itemStorage.create(item);
    }

    public Item update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        userStorage.checkUser(userId);
        itemStorage.itemCheck(itemId);

        Item item = itemStorage.getById(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У юзера нет этой вещи");
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }

        return itemStorage.update(item);
    }

    public List<Item> search(Long userId, String text) {
        userStorage.checkUser(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text);
    }

}
