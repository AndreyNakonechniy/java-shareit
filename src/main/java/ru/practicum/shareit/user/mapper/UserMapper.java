package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Component
public class UserMapper {
    public User userCreateDto(UserCreateDto userCreateDto) {
        return new User(userCreateDto.getName(), userCreateDto.getEmail());
    }

    public User userUpdate(UserUpdateDto userUpdateDto) {
        return new User(userUpdateDto.getName(), userUpdateDto.getEmail());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
