package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment fromCommentDtoPost(CommentDtoPost commentDtoPost, Item item, User user, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(created);
        comment.setText(commentDtoPost.getText());
        return comment;
    }
}
