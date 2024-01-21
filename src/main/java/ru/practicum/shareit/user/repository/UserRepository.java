package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select id from users", nativeQuery = true)
    List<Long> getIds();
}
