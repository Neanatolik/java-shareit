package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "select *\n" +
            "from comments c \n" +
            "where c.item_id = ?1", nativeQuery = true)
    List<Comment> getCommentsByItemId(long itemId);

    @Query(value = "select c.item_id\n" +
            "from comments c", nativeQuery = true)
    List<Long> getItemIds();
}
