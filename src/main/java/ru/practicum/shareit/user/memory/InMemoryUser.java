package ru.practicum.shareit.user.memory;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface InMemoryUser {
    User add(User user);

    User patch(User user, Long id);

    User getUserById(Long id);

    List<UserDto> getUsers();

    void deleteUser(Long id);
}
