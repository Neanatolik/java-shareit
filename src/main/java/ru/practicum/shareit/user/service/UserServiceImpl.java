package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.memory.InMemoryUserImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final InMemoryUserImpl inMemoryUser;
    private Long nextId = 0L;

    @Override
    public UserDto post(User user) {
        checkEmail(user.getEmail());
        checkUser(user);
        user.setId(getNextId());
        return UserMapper.toUserDto(inMemoryUser.add(user));
    }

    @Override
    public UserDto patch(User user, Long id) {
        User oldUser = inMemoryUser.getUserById(id);
        if (Objects.nonNull(user.getEmail()) && !user.getEmail().equals(oldUser.getEmail()))
            checkEmail(user.getEmail());
        return UserMapper.toUserDto(inMemoryUser.patch(updateUser(user, inMemoryUser.getUserById(id)), id));
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(inMemoryUser.getUserById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return inMemoryUser.getUsers();
    }

    @Override
    public void deleteUser(Long id) {
        inMemoryUser.deleteUser(id);
    }

    private User updateUser(User userNew, User userOld) {
        if (Objects.nonNull(userNew.getEmail())) userOld.setEmail(userNew.getEmail());
        if (Objects.nonNull(userNew.getName())) userOld.setName(userNew.getName());
        return userOld;
    }

    private void checkUser(User user) {
        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank()) {
            throw new BadRequest("User without email");
        }
    }

    private void checkEmail(String email) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequest("Wrong email");
        } else if (inMemoryUser.getEmails().contains(email)) {
            System.out.println(inMemoryUser.getEmails().contains(email));
            System.out.println(email);
            throw new ConflictException("User with this email already exists");
        }
    }

    private Long getNextId() {
        return ++nextId;
    }

}
