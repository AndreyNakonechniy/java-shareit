package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String userHeader = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(userHeader) Long userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(userHeader) Long userId, @Valid @RequestBody ItemCreateDto itemCreateDto, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        return itemService.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId, @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.update(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(userHeader) Long userId, @RequestParam String text) {
        return itemService.search(userId, text);
    }


}
