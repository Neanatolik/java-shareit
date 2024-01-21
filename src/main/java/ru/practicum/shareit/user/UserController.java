package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Validated(BasicInfo.class)
    public UserDto post(@RequestBody @Valid UserDto user) {
        log.info("POST /users");
        return userService.post(user);
    }


    @PatchMapping("/{id}")
    @Validated(AdvancedInfo.class)
    public UserDto patch(@RequestBody @Valid UserDto user, @PathVariable long id) {
        log.info("PATCH /users/{}", id);
        return userService.patch(user, id);
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
