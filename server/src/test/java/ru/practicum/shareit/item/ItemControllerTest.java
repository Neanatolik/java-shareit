package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private final String user = "X-Sharer-User-Id";
    @Autowired
    ObjectMapper mapper;
    private final ItemDto itemDto = new ItemDto(1L, "item1", "description: item1", true, null, null, Collections.emptyList(), null);
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void saveItem() throws Exception {
        when(itemService.saveItem(any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void getItemsByUserId() throws Exception {
        when(itemService.getItemsByUserId(anyLong()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void changeItem() throws Exception {
        when(itemService.changeItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void getItemByItemAndUserId() throws Exception {
        when(itemService.getItemByItemAndUserId(anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(get("/items/1")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));
    }

    @Test
    void searchByItemName() throws Exception {
        when(itemService.searchByItemName(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search")
                        .param("text", "item")
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    void postComment() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "text: comment1", "user1", LocalDateTime.now());
        when(itemService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header(user, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class));
    }
}