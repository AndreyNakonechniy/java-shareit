package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> argumentCaptor;
    private UserMapper mapper = new UserMapper();
    User user = new User(1L, "name", "user@user.ru");
    UserCreateDto userCreate = new UserCreateDto("name", "user@user.ru");

    @Test
    void getAll() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.getAll();
        assertEquals(users.stream().map(mapper::toUserDto).collect(Collectors.toList()), userDtos);
        assertEquals(1, userDtos.size());
    }

    @Test
    void getAllWithEmptyList() {
        List<User> users = List.of();
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.getAll();
        assertEquals(users.stream().map(mapper::toUserDto).collect(Collectors.toList()), userDtos);
        assertEquals(0, userDtos.size());
    }

    @Test
    void getById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.getById(1L);
        assertEquals(mapper.toUserDto(user), userDto);
    }

    @Test
    void getByIdWithException() {
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(0L));
    }

    @Test
    void create() {
        when(userRepository.save(mapper.userCreate(userCreate))).thenReturn(user);

        assertEquals(mapper.toUserDto(user), userService.create(userCreate));
        verify(userRepository).save(mapper.userCreate(userCreate));
    }

    @Test
    void update() {
        UserUpdateDto newUser = new UserUpdateDto("newName", "newEmail@email.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        userService.update(1L, newUser);
        verify(userRepository).save(argumentCaptor.capture());

        User updated = argumentCaptor.getValue();

        assertEquals("newName", updated.getName());
        assertEquals("newEmail@email.ru", updated.getEmail());
    }

    @Test
    void updateWithException() {
        UserUpdateDto newUser = new UserUpdateDto("newName", "newEmail@email.ru");

        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(0L, newUser));
    }

    @Test
    void delete() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);

    }

    @Test
    void deleteWithException() {
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(0L));

    }
}