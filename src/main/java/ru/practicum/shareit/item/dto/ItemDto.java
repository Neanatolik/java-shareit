package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
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
    @Positive(groups = AdvancedInfo.class)
    @NotNull(groups = AdvancedInfo.class)
    private Long request;
}
