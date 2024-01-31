package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    UserDto saveUser(@Valid UserDto user);

    UserDto changeUser(@Valid UserDto user, long id);

    UserDto getUser(long id);

    List<UserDto> getUsers();

    void deleteUser(long id);
}
