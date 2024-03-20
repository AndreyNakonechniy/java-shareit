package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserCreateDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

@Component
public class UserMapper {
    public UserDto userCreateDto(UserCreateDto userCreateDto) {
        return new UserDto(userCreateDto.getName(), userCreateDto.getEmail());
    }

    public UserDto userUpdate(UserUpdateDto userUpdateDto) {
        return new UserDto(userUpdateDto.getName(), userUpdateDto.getEmail());
    }
}
