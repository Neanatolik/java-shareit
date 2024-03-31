package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoPost;
import ru.practicum.shareit.item.dto.ItemDtoPost;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX)).requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> saveBooking(BookingDto bookingDto, long userId) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> getBookingWithState(long userId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> saveItem(ItemDtoPost itemDtoPost, long userId) {
        return post("", userId, itemDtoPost);
    }

    public ResponseEntity<Object> getItemsByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> changeItem(ItemDtoPost itemDtoPost, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDtoPost);
    }

    public ResponseEntity<Object> getItemByItemAndUserId(Long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchByItemName(String text, long userId, int from, int size) {
        Map<String, Object> parameters = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> postComment(long userId, Long itemId, CommentDtoPost comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
