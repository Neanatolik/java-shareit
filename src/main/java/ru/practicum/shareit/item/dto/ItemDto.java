package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @Positive(groups = AdvancedInfo.class)
    @NotNull(groups = AdvancedInfo.class)
    private Long id;
    @NotBlank(groups = {BasicInfo.class, AdvancedInfo.class})
    private String name;
    @NotBlank(groups = {BasicInfo.class, AdvancedInfo.class})
    private String description;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private Boolean available;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
