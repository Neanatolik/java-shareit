package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final TestEntityManager em;
    private final UserRepository repository;

    @Test
    void existUserId() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        em.persist(user);
        assertTrue(repository.existUserId(user.getId()));
        assertFalse(repository.existUserId(0));
    }
}