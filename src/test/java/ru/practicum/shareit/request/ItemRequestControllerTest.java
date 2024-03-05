package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final String user = "X-Sharer-User-Id";
    private final User user1 = new User(1L, "user1", "user1@mail.com");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description1", user1, LocalDateTime.now(), Collections.emptyList());
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveItemRequest() throws Exception {
        when(itemRequestService.saveItemRequest(any(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestDto))
                .header(user, 1L).characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequestsByOwner(anyLong())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestsByOwnerById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    void getAllItemRequestsPage() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                .header(user, 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class));
    }
}