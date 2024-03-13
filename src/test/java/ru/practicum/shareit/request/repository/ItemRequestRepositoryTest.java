package ru.practicum.shareit.request.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {
    private final TestEntityManager em;
    private final ItemRequestRepository repository;

    @Test
    void findAllItemRequestByRequestor() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        em.persist(user);
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        em.persist(user2);
        ItemRequest itemRequest = makeItemRequest(user, "itemRequest1", LocalDateTime.now());
        ItemRequest itemRequest2 = makeItemRequest(user, "itemRequest2", LocalDateTime.now());
        ItemRequest itemRequest3 = makeItemRequest(user2, "itemRequest3", LocalDateTime.now());
        List<ItemRequest> sourceItemRequests = List.of(itemRequest, itemRequest2);
        em.persist(itemRequest);
        em.persist(itemRequest2);
        em.persist(itemRequest3);

        List<ItemRequest> targetItemRequests = repository.findAllItemRequestByRequestor(user);

        assertThat(targetItemRequests, hasSize(sourceItemRequests.size()));

        for (ItemRequest sourceItemRequest : sourceItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription()))
            )));
        }
    }

    @Test
    void findAllItemRequest() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        em.persist(user);
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        em.persist(user2);
        ItemRequest itemRequest = makeItemRequest(user, "itemRequest1", LocalDateTime.now());
        ItemRequest itemRequest2 = makeItemRequest(user, "itemRequest2", LocalDateTime.now());
        ItemRequest itemRequest3 = makeItemRequest(user2, "itemRequest3", LocalDateTime.now());
        em.persist(itemRequest);
        em.persist(itemRequest2);
        em.persist(itemRequest3);
        List<ItemRequest> sourceItemRequests = List.of(itemRequest, itemRequest2);

        List<ItemRequest> targetItemRequests = repository.findAllItemRequest(user2.getId(), PageRequest.of(0, 10));

        assertThat(targetItemRequests, hasSize(sourceItemRequests.size()));

        for (ItemRequest sourceItemRequest : sourceItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription()))
            )));
        }
    }

    @Test
    void existItemById() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        em.persist(user);
        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        em.persist(user2);
        ItemRequest itemRequest = makeItemRequest(user, "itemRequest1", LocalDateTime.now());
        ItemRequest itemRequest2 = makeItemRequest(user, "itemRequest2", LocalDateTime.now());
        ItemRequest itemRequest3 = makeItemRequest(user2, "itemRequest3", LocalDateTime.now());
        em.persist(itemRequest);
        em.persist(itemRequest2);
        em.persist(itemRequest3);
        assertFalse(repository.existItemById(itemRequest.getId()));
        assertTrue(repository.existItemById(0));

    }

    private ItemRequest makeItemRequest(User user, String description, LocalDateTime localDateTime) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(localDateTime);
        itemRequest.setDescription(description);
        itemRequest.setRequestor(user);
        return itemRequest;
    }
}