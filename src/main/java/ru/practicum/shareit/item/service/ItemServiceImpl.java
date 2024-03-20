package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private UserStorage userStorage;
    private ItemStorage itemStorage;
    private ItemMapper mapper;

    public List<ItemDto> getOwnerItems(Long userId) {
        userStorage.checkUser(userId);
        return itemStorage.getOwnerItems(userId);
    }

    public ItemDto getById(Long userId, Long itemId) {
        userStorage.checkUser(userId);
        return itemStorage.getById(itemId);
    }

    public ItemDto create(Long userId, ItemCreateDto itemCreateDto) {
        userStorage.checkUser(userId);
        UserDto userDto = userStorage.getUserById(userId);
        ItemDto itemDto = mapper.itemFromDto(itemCreateDto);
        itemDto.setOwner(userDto);
        return itemStorage.create(itemDto);
    }

    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        userStorage.checkUser(userId);
        itemStorage.itemCheck(itemId);

        return itemStorage.update(userId, itemId, itemUpdateDto);
    }

    public List<ItemDto> search(Long userId, String text) {
        userStorage.checkUser(userId);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text);
    }

}
