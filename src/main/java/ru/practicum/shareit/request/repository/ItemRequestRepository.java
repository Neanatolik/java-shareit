package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllItemRequestByRequestor(User user);

    @Query(value = "SELECT *\n" + "FROM ITEM_REQUESTS ir\n" + "WHERE ir.REQUESTOR_ID <> ?1", nativeQuery = true)
    List<ItemRequest> findAllItemRequest(Long userId, PageRequest page);

    @Query(value = "SELECT NOT EXISTS (SELECT ir.id \n" + "FROM ITEM_REQUESTS ir\n" + "WHERE ir.id = ?1)", nativeQuery = true)
    boolean existItemById(long itemRequestId);
}
