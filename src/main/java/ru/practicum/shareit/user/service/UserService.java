package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto post(User user);

    UserDto patch(User user, Long id);

    UserDto getUser(Long id);

    List<UserDto> getUsers();

    void deleteUser(Long id);
}
