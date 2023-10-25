package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public User fromUserDto(UserDto userDto, long id) {
        return new User(
                id,
                userDto.getName(),
                userDto.getEmail());
    }

}
