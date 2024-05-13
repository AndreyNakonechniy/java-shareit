package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    RequestMapper mapper = new RequestMapper();


    User requester = new User(2L, "name", "email@email.com");
    User owner = new User(1L, "owner", "owner@owner.com");
    Item item = new Item(1L, "name", "description", true, owner, 1L);

    static LocalDateTime testTime = LocalDateTime.of(2023, 12, 12, 12, 12);

    PageRequest defaultPageRequest = PageRequest.of(0, 10);

    ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now(), List.of(item));
    ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description");

    @BeforeAll
    static void changeTime() {
        MockedStatic<LocalDateTime> mockTime = Mockito.mockStatic(LocalDateTime.class);
        mockTime.when(LocalDateTime::now).thenReturn(testTime);
    }

    @Test
    void create() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(requester));

        when(itemRequestRepository.save(mapper.toItemRequest(itemRequestCreateDto, requester, LocalDateTime.now()))).thenReturn(itemRequest);
        assertEquals(mapper.toItemRequestDto(itemRequest), itemRequestService.create(2L, itemRequestCreateDto));
        verify(itemRequestRepository).save(mapper.toItemRequest(itemRequestCreateDto, requester, LocalDateTime.now()));
    }

    @Test
    void createWithException() {
        when(userRepository.findById(0L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(0L, itemRequestCreateDto));

    }

    @Test
    void getUserRequests() {
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findByRequesterId(2L, defaultPageRequest)).thenReturn(itemRequests);
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getUserRequests(2L, 0, 10);

        assertEquals(itemRequests.stream().map(mapper::toItemRequestDto).collect(Collectors.toList()), itemRequestDtos);
    }

    @Test
    void getUserRequestsWithException() {
        when(userRepository.findById(0L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(0L, 0, 10));
    }

    @Test
    void getAllRequests() {
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.findByItemsOwnerId(1L, defaultPageRequest)).thenReturn(itemRequests);
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllRequests(1L, 0, 10);

        assertEquals(itemRequests.stream().map(mapper::toItemRequestDto).collect(Collectors.toList()), itemRequestDtos);
    }

    @Test
    void getAllRequestsWithException() {
        when(userRepository.findById(0L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(0L, 0, 10));
    }

    @Test
    void getById() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(itemRequest));

        ItemRequestDto itemRequestDto = itemRequestService.getById(2L, 1L);

        assertEquals(mapper.toItemRequestDto(itemRequest), itemRequestDto);
    }

    @Test
    void getByIdWithUserNotFoundException() {
        when(userRepository.findById(0L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(0L, 1L));
    }

    @Test
    void getByIdWithItemRequestNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findById(0L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(2L, 0L));
    }
}