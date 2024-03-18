package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

@Component
public class UserMapper {
    public User userFromDto(UserDto userDto) {
        return new User(userDto.getName(), userDto.getEmail());
    }
}
