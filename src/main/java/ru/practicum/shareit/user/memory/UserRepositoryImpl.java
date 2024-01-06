package ru.practicum.shareit.user.memory;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> userHashMap = new HashMap<>();
    @Getter
    private final Set<String> emails = new HashSet<>();

    @Override
    public User add(User user) {
        userHashMap.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User patch(User user, long id) {
        emails.remove(userHashMap.get(id).getEmail());
        userHashMap.put(user.getId(), user);
        emails.add(user.getEmail());
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
        emails.remove(userHashMap.get(id).getEmail());
        userHashMap.remove(id);
    }

    public List<Long> getIds() {
        return new ArrayList<>(userHashMap.keySet());
    }


}
