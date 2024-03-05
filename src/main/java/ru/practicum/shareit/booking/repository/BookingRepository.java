package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where i.owner_id  = ?1\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getOwnersItemAll(long userId, Pageable page);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where i.owner_id  = ?1 and\n" +
            "b.end_date < now()\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getOwnersItemPast(long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where i.owner_id  = ?1\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getOwnersItemFuture(long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where i.owner_id  = ?1 and\n" +
            "b.start_date < now() and\n" +
            "b.end_date > now()\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getOwnersItemCurrent(long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where i.owner_id  = ?1 and b.status = ?2\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getBookingsByStatusAndOwnerId(long userId, String status);

    @Query(value = "select b.booker_id\n" +
            "from bookings b\n" +
            "where b.id = ?1", nativeQuery = true)
    Long getBookersFromBookingByBookingId(long bookingId);

    @Query(value = "select i.owner_id\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where b.id = ?1", nativeQuery = true)
    Long getOwnersFromBookingByBookingId(long bookingId);

    @Query(value = "select b.status \n" +
            "from bookings b\n" +
            "where b.id = ?1", nativeQuery = true)
    Status getStatusByBookingId(Long bookingId);

    @Query(value = "select b.booker_id\n" +
            "from bookings b \n" +
            "where b.item_id = ?1 and \n" +
            "b.status = 'APPROVED'", nativeQuery = true)
    List<Long> getBookerByItemId(Long itemId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "where b.item_id = ?2 and \n" +
            "b.booker_id = ?1 and \n" +
            "b.status = 'APPROVED' and \n" +
            "b.start_date < now()", nativeQuery = true)
    List<Booking> getBookingsByStatusAndUserAndItemAndTime(long userId, long itemId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where b.item_id = ?1 and \n" +
            "i.owner_id = ?2\n" +
            "order by b.start_date asc", nativeQuery = true)
    List<Booking> getBookingsByItemAndUserId(long itemId, long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where b.item_id = ?1 and \n" +
            "i.owner_id = ?2 and\n" +
            "b.status = 'APPROVED'\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getBookingsByItemIdDesc(long itemId, long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "where b.booker_id = ?1\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getBookingsItemAll(long userId, Pageable page);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "where b.booker_id  = ?1 and b.status = ?2\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getBookingsByStatusAndUserId(long userId, String status);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "where b.booker_id = ?1 and\n" +
            "b.start_date > now()\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getBookingsItemFuture(long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "where b.booker_id = ?1 and\n" +
            "b.start_date < now() and\n" +
            "b.end_date > now()\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getBookingsCurrentByUserId(long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "where b.booker_id = ?1 and\n" +
            "b.end_date < now()\n" +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> getBookingsItemPast(long userId);

    @Query(value = "select *\n" +
            "from bookings b\n" +
            "left join items i on b.item_id  = i.id\n" +
            "where i.owner_id = ?1\n" +
            "order by b.start_date asc", nativeQuery = true)
    List<Booking> getBookingsByUserId(long userId);
}
