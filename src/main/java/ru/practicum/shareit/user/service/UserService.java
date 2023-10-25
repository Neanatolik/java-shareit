package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    UserDto post(@Valid UserDto user);

    UserDto patch(@Valid UserDto user, long id);

    UserDto getUser(long id);

    List<UserDto> getUsers();

    void deleteUser(long id);
}
