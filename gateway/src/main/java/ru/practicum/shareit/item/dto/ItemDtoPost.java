package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoPost {
    @NotNull
    @NotBlank
    private String name;
    @NotBlank
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
