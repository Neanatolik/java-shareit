package ru.practicum.shareit.user.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.memory.UserRepositoryImpl;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl inMemoryUser;
    private long nextId = 0L;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl inMemoryUser) {
        this.inMemoryUser = inMemoryUser;
    }

    @Override
    public UserDto post(@Valid UserDto user) {
        checkEmail(user.getEmail());
        checkUser(user);
        return UserMapper.toUserDto(inMemoryUser.add(UserMapper.fromUserDto(user, getNextId())));
    }

    @Override
    public UserDto patch(@Valid UserDto user, long id) {
        User oldUser = inMemoryUser.getUserById(id);
        if (Objects.nonNull(user.getEmail()) && !user.getEmail().equals(oldUser.getEmail()))
            checkEmail(user.getEmail());
        return UserMapper.toUserDto(inMemoryUser.patch(updateUser(UserMapper.fromUserDto(user, id), inMemoryUser.getUserById(id)), id));
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.toUserDto(inMemoryUser.getUserById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return inMemoryUser.getUsers();
    }

    @Override
    public void deleteUser(long id) {
        inMemoryUser.deleteUser(id);
    }

    private User updateUser(User userNew, User userOld) {
        if (Objects.isNull(userNew.getEmail())) userNew.setEmail(userOld.getEmail());
        if (Objects.isNull(userNew.getName())) userNew.setName(userOld.getName());
        return userNew;
    }

    private void checkUser(@Valid UserDto user) {
        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank()) {
            throw new BadRequest("User without email");
        }
    }

    private void checkEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequest("Wrong email");
        } else if (inMemoryUser.getEmails().contains(email)) {
            throw new ConflictException("User with this email already exists");
        }
    }

    private long getNextId() {
        return ++nextId;
    }

}
