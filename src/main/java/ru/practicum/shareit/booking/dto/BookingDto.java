package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.enums.Status;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    @Positive(groups = AdvancedInfo.class)
    @NotNull(groups = AdvancedInfo.class)
    private Long id;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private LocalDateTime start;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private LocalDateTime end;
    @Positive(groups = {BasicInfo.class, AdvancedInfo.class})
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private Long itemId;
    @Positive(groups = {BasicInfo.class, AdvancedInfo.class})
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private Long booker;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private Status status;

}
