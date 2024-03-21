package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
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
    public Item getById(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader(userHeader) Long userId, @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId, @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.update(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestHeader(userHeader) Long userId, @RequestParam String text) {
        return itemService.search(userId, text);
    }


}
