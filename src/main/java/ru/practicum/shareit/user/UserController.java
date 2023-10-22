package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private final UserService userService;


    @PostMapping
    public UserDto post(@Valid @RequestBody User user) {
        log.info("POST /users");
        return userService.post(user);
    }

    @PatchMapping("/{id}")
    public UserDto patch(@Valid @RequestBody User user, @PathVariable Long id) {
        log.info("PATCH /users/{}", id);
        return userService.patch(user, id);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("GET /users/{}", id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET /users/");
        return userService.getUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{}", id);
        userService.deleteUser(id);
    }

}
