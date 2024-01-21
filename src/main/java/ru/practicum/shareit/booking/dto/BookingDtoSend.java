package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.constaints.AdvancedInfo;
import ru.practicum.shareit.constaints.BasicInfo;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoSend {
    @Positive(groups = AdvancedInfo.class)
    @NotNull(groups = AdvancedInfo.class)
    private Long id;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private LocalDateTime start;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private LocalDateTime end;
    @Positive(groups = {BasicInfo.class, AdvancedInfo.class})
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private ItemDtoForBooking item;
    @Positive(groups = {BasicInfo.class, AdvancedInfo.class})
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private UserDto booker;
    @NotNull(groups = {BasicInfo.class, AdvancedInfo.class})
    private Status status;
}
