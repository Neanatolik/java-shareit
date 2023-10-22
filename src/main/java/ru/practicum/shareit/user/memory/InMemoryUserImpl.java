package ru.practicum.shareit.user.memory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryUserImpl implements InMemoryUser {
    private final HashMap<Long, User> userHashMap = new HashMap<>();

    @Override
    public User add(User user) {
        userHashMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User patch(User user, Long id) {
        userHashMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userHashMap.get(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return new ArrayList<>(userHashMap.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList()));
    }

    @Override
    public void deleteUser(Long id) {
        userHashMap.remove(id);
    }

    public ArrayList<String> getEmails() {
        return new ArrayList<>(userHashMap.values().stream().map(User::getEmail).collect(Collectors.toUnmodifiableList()));
    }

    public ArrayList<Long> getIds() {
        return new ArrayList<>(userHashMap.keySet());
    }


}
