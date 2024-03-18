package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String userHeader = "X-Sharer-User-Id";

    @GetMapping
    public List<Item> getOwnerItems(@RequestHeader(userHeader) Long userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public Item createItem(@RequestHeader(userHeader) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestHeader(userHeader) Long userId, @RequestParam String text) {
        return itemService.search(userId, text);
    }


}
