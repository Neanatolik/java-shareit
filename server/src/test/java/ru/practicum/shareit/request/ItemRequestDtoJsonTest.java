package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testUserDto() throws Exception {
        User user = new User(1L, "User", "User@mail.com");
        LocalDateTime created = LocalDateTime.of(2024, Month.APRIL, 3, 15, 30, 20);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "ItemRequest description", user, created, null);
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ItemRequest description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-04-03 15:30:20");
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("User");
    }
}
