package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper mapper = new UserMapper();
    private final UserRepository repository;

    public List<UserDto> getAll() {
        List<User> users = repository.findAll();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(mapper.toUserDto(user));
        }
        return usersDto;
    }

    public UserDto getById(Long id) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });
        return mapper.toUserDto(user);
    }

    public UserDto create(UserCreateDto userCreateDto) {
        User user = repository.save(mapper.userCreate(userCreateDto));
        return mapper.toUserDto(user);
    }

    public UserDto update(Long id, UserUpdateDto userUpdateDto) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });

        User userUpdate = mapper.userUpdate(userUpdateDto);
        userUpdate.setId(id);
        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            user.setEmail(userUpdate.getEmail());
        }
        repository.save(user);
        return mapper.toUserDto(user);
    }

    public void delete(Long id) {
        repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Нет пользователя с таким id");
        });
        repository.deleteById(id);
    }

}
