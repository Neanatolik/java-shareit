package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;
    private final UserDto userDto1 = makeUserDto("userDto1", "userDto1@mail.com");

    @Test
    @Order(1)
    void saveUser() {
        UserDto userDtoWrong1 = new UserDto();
        assertThrows(BadRequest.class, () -> service.saveUser(userDtoWrong1));

        userDtoWrong1.setEmail("userDtoWrong1@mail.com");
        assertThrows(BadRequest.class, () -> service.saveUser(userDtoWrong1));

        userDtoWrong1.setEmail("userDtoWrong1Email");
        userDtoWrong1.setName("userDtoWrong1Name");
        assertThrows(BadRequest.class, () -> service.saveUser(userDtoWrong1));

        service.saveUser(userDto1);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));

    }

    @Test
    @Order(2)
    void changeUser() {
        userDto1.setName("newName");
        User user1 = UserMapper.fromUserDto(userDto1);
        em.persist(user1);
        service.changeUser(userDto1, user1.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));

    }

    @Test
    @Order(3)
    void getUser() {
        User user1 = UserMapper.fromUserDto(userDto1);
        em.persist(user1);
        UserDto userGet = service.getUser(user1.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail())
                .getSingleResult();

        assertThat(user.getId(), equalTo(userGet.getId()));
        assertThat(user.getName(), equalTo(userGet.getName()));
        assertThat(user.getEmail(), equalTo(userGet.getEmail()));
    }

    @Test
    @Order(4)
    void getUsers() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("userDto1", "userDto1@mail.com"),
                makeUserDto("userDto2", "userDto2@mail.com"),
                makeUserDto("userDto3", "userDto3@mail.com")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.fromUserDto(user);
            em.persist(entity);
        }
        em.flush();

        List<UserDto> targetUsers = service.getUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));

        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    @Order(5)
    void deleteUser() {
        User user1 = UserMapper.fromUserDto(userDto1);
        em.persist(user1);
        service.deleteUser(user1.getId());
        List<UserDto> targetUsers = service.getUsers();
        assertThat(targetUsers, hasSize(0));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

}