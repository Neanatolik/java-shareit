package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated());
    }

    public Comment fromCommentDto(CommentDto commentDto, long id, long userId, long itemId) {
        return new Comment(
                id,
                commentDto.getText(),
                itemId,
                userId,
                commentDto.getCreated());
    }
}
