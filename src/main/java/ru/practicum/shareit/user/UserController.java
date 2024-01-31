package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Validated(BasicInfo.class)
    public UserDto saveUser(@RequestBody @Valid UserDto user) {
        log.info("POST /users");
        return userService.saveUser(user);
    }


    @PatchMapping("/{id}")
    @Validated(BasicInfo.class)
    public UserDto changeUser(@RequestBody @Valid UserDto user, @PathVariable long id) {
        log.info("PATCH /users/{}", id);
        return userService.changeUser(user, id);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("GET /users/{}", id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET /users/");
        return userService.getUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("DELETE /users/{}", id);
        userService.deleteUser(id);
    }

}
