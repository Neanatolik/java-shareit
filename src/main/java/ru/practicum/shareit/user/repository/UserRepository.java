package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT EXISTS(select u.id\n" +
            "from users u\n" +
            "where u.id = ?1)", nativeQuery = true)
    boolean existUserId(long id);
}
