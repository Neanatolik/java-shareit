package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoPostForRequest {
    @NotBlank(groups = {BasicInfo.class, AdvancedInfo.class})
    private Long id;
    @NotBlank(groups = {BasicInfo.class, AdvancedInfo.class})
    private String name;
    @NotBlank(groups = {BasicInfo.class, AdvancedInfo.class})
    private String description;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private Boolean available;
    private Long requestId;
}
