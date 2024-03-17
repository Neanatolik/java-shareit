package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select *\n" +
            "from items i\n" +
            "where owner_id = ?1\n" +
            "order by i.id asc ", nativeQuery = true)
    List<Item> getItems(long userId);

    @Query(value = "select *\n" +
            "from items\n" +
            "where lower(description) like lower(concat('%',?1,'%'))\n" +
            "and is_available is TRUE", nativeQuery = true)
    Page<Item> search(String itemName, PageRequest page);

    List<Item> findAllItemByRequestIdIsNotNull();

    Item getItemByRequestId(Long itemRequestId);

    List<Item> findAllItemByRequestId(Long itemRequestId);
}
