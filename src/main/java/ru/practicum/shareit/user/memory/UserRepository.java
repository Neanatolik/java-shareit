package ru.practicum.shareit.user.memory;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User add(User user);

    User patch(User user, long id);

    User getUserById(long id);

    List<UserDto> getUsers();

    void deleteUser(long id);
}
