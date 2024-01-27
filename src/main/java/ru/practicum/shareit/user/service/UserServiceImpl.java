package ru.practicum.shareit.user.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private long nextId = 0L;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto saveUser(@Valid UserDto user) {
        checkEmail(user.getEmail());
        checkUser(user);
        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(user, getNextId())));
    }

    @Override
    @Transactional
    public UserDto changeUser(@Valid UserDto user, long id) {
        if (!Objects.isNull(user.getEmail())) checkEmail(user.getEmail());
        User newUser = updateUser(UserMapper.fromUserDto(user, id), getUserById(id));
        userRepository.save(newUser);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким id не существует"));
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
        }
    }

    private long getNextId() {
        return ++nextId;
    }

}
