package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private static final String userHeader = "X-Sharer-User-Id";


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(userHeader) Long userId, @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Post запрос на добавление вещи");
        return itemService.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId, @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Path запрос на обновление вещи с id: {}", itemId);
        return itemService.update(userId, itemId, itemUpdateDto);
    }

    @GetMapping
    public List<ItemBookingDto> getOwnerItems(@RequestHeader(userHeader) Long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("Get запрос на получение всех вещей пользователя с id: {}", userId);
        return itemService.getOwnerItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getById(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId) {
        log.info("Get запрос на получение вещи с id: {}", itemId);
        return itemService.getById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(userHeader) Long userId, @RequestParam String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("Get запрос на получение всех вещей, имя или описание которых содержит текст: {}", text);
        return itemService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(userHeader) Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Post запрос на добавление комментария к вещи с id: {}", itemId);
        return itemService.addComment(userId, itemId, commentCreateDto);
    }

}
