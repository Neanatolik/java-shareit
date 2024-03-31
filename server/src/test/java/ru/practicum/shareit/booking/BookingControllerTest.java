package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSend;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private final String user = "X-Sharer-User-Id";
    private final ItemDtoForBooking item1 = new ItemDtoForBooking(1L, "item1");
    private final UserDto userDto = new UserDto(1L, "user1", "user1@mail.com");
    private final BookingDtoSend bookingDtoSend = new BookingDtoSend(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item1, userDto, Status.APPROVED);
    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 1L, Status.APPROVED);
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveBooking() throws Exception {
        when(bookingService.saveBooking(any(), anyLong()))
                .thenReturn(bookingDtoSend);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDtoSend.getId()), Long.class));
    }

    @Test
    void getBookingWithState() throws Exception {
        when(bookingService.getBookingWithState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoSend));
        mvc.perform(get("/bookings")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(bookingDtoSend.getId()), Long.class));
    }

    @Test
    void changeBooking() throws Exception {
        when(bookingService.changeBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDtoSend);
        mvc.perform(patch("/bookings/1")
                        .header(user, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDtoSend.getId()), Long.class));
    }

    @Test
    void getBookingWithoutState() throws Exception {
        when(bookingService.getBookingWithoutState(anyLong(), anyLong()))
                .thenReturn(bookingDtoSend);
        mvc.perform(get("/bookings/1")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDtoSend.getId()), Long.class));
    }

    @Test
    void getOwnersItem() throws Exception {
        when(bookingService.getOwnersItem(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoSend));
        mvc.perform(get("/bookings/owner")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(bookingDtoSend.getId()), Long.class));
    }
}