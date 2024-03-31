package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User fromUserDto(UserDto bookingDto) {
        User user = new User();
        user.setId(bookingDto.getId());
        user.setName(bookingDto.getName());
        user.setEmail(bookingDto.getEmail());
        return user;
    }

    public User fromUserDto(UserDto bookingDto, long id) {
        User user = new User();
        user.setId(id);
        user.setName(bookingDto.getName());
        user.setEmail(bookingDto.getEmail());
        return user;
    }

}
