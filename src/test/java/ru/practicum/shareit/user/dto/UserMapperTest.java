package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class UserMapperTest {

    @Test
    void toUserDto() {
        User user = new User(1L, "User", "User@email.com");
        UserDto userDto = UserMapper.toUserDto(user);
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
        assertThat(userDto.getName(), equalTo(user.getName()));
    }

    @Test
    void fromUserDto() {
        UserDto userDto = new UserDto(1L, "User", "User@email.com");
        User user = UserMapper.fromUserDto(userDto, userDto.getId());
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getName(), equalTo(userDto.getName()));
    }

    @Test
    void testFromUserDto() {
        UserDto userDto = new UserDto(1L, "User", "User@email.com");
        User user = UserMapper.fromUserDto(userDto);
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getName(), equalTo(userDto.getName()));
    }
}