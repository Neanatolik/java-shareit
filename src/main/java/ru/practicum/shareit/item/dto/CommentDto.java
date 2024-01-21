package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.constaints.AdvancedInfo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    @Positive(groups = AdvancedInfo.class)
    @NotNull(groups = AdvancedInfo.class)
    private Long id;
    @NotNull(groups = AdvancedInfo.class)
    private String text;
    @Positive(groups = AdvancedInfo.class)
    @NotNull(groups = AdvancedInfo.class)
    private String authorName;
    @NotNull(groups = AdvancedInfo.class)
    private LocalDateTime created;
}
