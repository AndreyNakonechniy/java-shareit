package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private RequestMapper requestMapper = new RequestMapper();


    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });
        ItemRequest itemRequest = itemRequestRepository.save(requestMapper.toItemRequest(itemRequestCreateDto, user, LocalDateTime.now()));

        return requestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });
        if (from < 0 || size <= 0) {
            throw new ValidationException("Некоректные данные");
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(userId, PageRequest.of(from / size, size));
        return itemRequests.stream().map(requestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });

        if (from < 0 || size <= 0) {
            throw new ValidationException("Некоректные данные");
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findByItemsOwnerId(userId, PageRequest.of(from / size, size));
        return itemRequests.stream().map(requestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого пользователя");
        });
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            throw new NotFoundException("Нет такого запроса");
        });
        return requestMapper.toItemRequestDto(itemRequest);
    }
}
