package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    @Validated(BasicInfo.class)
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto user) {
        log.info("POST /users");
        return userClient.saveUser(user);
    }


    @PatchMapping("/{id}")
    @Validated(BasicInfo.class)
    public ResponseEntity<Object> changeUser(@RequestBody @Valid UserDto user, @PathVariable long id) {
        log.info("PATCH /users/{}", id);
        return userClient.changeUser(user, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("GET /users/{}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("GET /users/");
        return userClient.getUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("DELETE /users/{}", id);
        userClient.deleteUser(id);
    }

}
