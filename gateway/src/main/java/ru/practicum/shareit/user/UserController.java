package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto user) {
        log.info("POST /users");
        return userClient.saveUser(user);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> changeUser(@RequestBody UserDto user, @Positive @PathVariable long id) {
        log.info("PATCH /users/{}", id);
        return userClient.changeUser(user, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@Positive @PathVariable long id) {
        log.info("GET /users/{}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("GET /users/");
        return userClient.getUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Positive @PathVariable long id) {
        log.info("DELETE /users/{}", id);
        userClient.deleteUser(id);
    }

}
