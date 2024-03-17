package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.BadRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoPost;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService service;

    @Test
    @Order(1)
    void saveItemRequest() {
        User user = makeUser("userDto1", "userDto1@mail.com");

        ItemRequestDtoPost itemRequestDtoWrong = new ItemRequestDtoPost();
        assertThrows(BadRequest.class, () -> service.saveItemRequest(itemRequestDtoWrong, 1L));
        itemRequestDtoWrong.setDescription("Wrong itemRequest");
        assertThrows(NotFoundException.class, () -> service.saveItemRequest(itemRequestDtoWrong, 1L));
        em.persist(user);
        ItemRequestDtoPost itemRequestDto1 = makeItemRequestDtoPost("Item Request 1");
        service.saveItemRequest(itemRequestDto1, user.getId());
        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.description = :description", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("description", itemRequestDto1.getDescription())
                .getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto1.getDescription()));

    }

    @Test
    @Order(2)
    void getItemRequestsByOwner() {
        assertThrows(NotFoundException.class, () -> service.getItemRequestsByOwner(0L));

        User user = makeUser("user1", "user1@mail.com");
        User user2 = makeUser("user2", "user2@mail.com");

        List<ItemRequestDto> sourceItemRequests = List.of(
                makeItemRequestDto("Item Request 1", user),
                makeItemRequestDto("Item Request 2", user),
                makeItemRequestDto("Item Request 3", user2)
        );

        em.persist(user);
        em.persist(user2);

        for (ItemRequestDto itemRequestDto : sourceItemRequests) {
            ItemRequest entity = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
            em.persist(entity);
        }
        em.flush();

        List<ItemRequestDto> targetItemRequests = service.getItemRequestsByOwner(user.getId());
        List<ItemRequestDto> user1ItemRequests = sourceItemRequests.stream()
                .filter(ir -> ir.getRequestor().getName().equals("user1")).collect(Collectors.toList());
        assertThat(targetItemRequests, hasSize(user1ItemRequests.size()));
        for (ItemRequestDto itemRequestDto : user1ItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemRequestDto.getDescription()))
            )));
        }
        ItemRequestDto itemRequestDto = makeItemRequestDto("itemRequestDto", user2);
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
        em.persist(itemRequest);
        Item item = makeItem("itemName", "itemDescription", true, user, itemRequest.getId());
        em.persist(item);

    }

    @Test
    @Order(3)
    void getAllItemRequests() {
        assertThrows(BadRequest.class, () -> service.getAllItemRequests(2L, -1, -1));
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);

        List<ItemRequestDto> sourceItemRequests = List.of(
                makeItemRequestDto("Item Request 1", user),
                makeItemRequestDto("Item Request 2", user),
                makeItemRequestDto("Item Request 3", user)
        );

        for (ItemRequestDto itemRequestDto : sourceItemRequests) {
            ItemRequest entity = ItemRequestMapper.fromItemRequestDto(itemRequestDto);
            em.persist(entity);
        }
        em.flush();

        Item item = makeItem("item1", "item1", true, user, 7L);
        em.persist(item);
        Item item2 = makeItem("item1", "item1", true, user, 7L);
        em.persist(item2);

        List<ItemRequestDto> targetItemRequests = service.getAllItemRequests(user.getId(), 0, 10);
        assertThat(targetItemRequests, hasSize(targetItemRequests.size()));
        for (ItemRequestDto itemRequestDto : targetItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(itemRequestDto.getDescription()))
            )));
        }
    }

    @Test
    @Order(4)
    void getItemRequestsByOwnerById() {
        assertThrows(NotFoundException.class, () -> service.getItemRequestsByOwnerById(5L, 0L));
        User user = makeUser("user1", "user1@mail.com");
        em.persist(user);
        assertThrows(NotFoundException.class, () -> service.getItemRequestsByOwnerById(user.getId(), 0L));
        ItemRequestDto itemRequestDto1 = makeItemRequestDto("Item Request 1", user);
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto1);
        em.persist(itemRequest);
        Item item = makeItem("item1", "item1", true, user, itemRequest.getId());
        em.persist(item);
        ItemRequestDto itemRequestDto = service.getItemRequestsByOwnerById(user.getId(), itemRequest.getId());
        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestDto1.getDescription()));
    }

    private ItemRequestDtoPost makeItemRequestDtoPost(String description) {
        ItemRequestDtoPost itemRequestDtoPost = new ItemRequestDtoPost();
        itemRequestDtoPost.setDescription(description);
        return itemRequestDtoPost;
    }

    private ItemRequestDto makeItemRequestDto(String description, User requestor) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        itemRequestDto.setRequestor(requestor);
        itemRequestDto.setCreated(LocalDateTime.now());
        return itemRequestDto;
    }

    private User makeUser(String name, String email) {
        User dto = new User();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private Item makeItem(String name, String description, boolean isAvailable, User owner, Long requestId) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(isAvailable);
        item.setOwner(owner);
        item.setRequestId(requestId);
        return item;
    }

}