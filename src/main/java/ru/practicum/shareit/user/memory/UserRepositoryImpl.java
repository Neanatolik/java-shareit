package ru.practicum.shareit.user.memory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> userHashMap = new HashMap<>();

    @Override
    public User add(User user) {
        userHashMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User patch(User user, long id) {
        userHashMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        return userHashMap.get(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return new ArrayList<>(userHashMap.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList()));
    }

    @Override
    public void deleteUser(long id) {
        userHashMap.remove(id);
    }

    public Set<String> getEmails() {
        return userHashMap.values().stream().map(User::getEmail).collect(Collectors.toSet());
    }

    public ArrayList<Long> getIds() {
        return new ArrayList<>(userHashMap.keySet());
    }


}
