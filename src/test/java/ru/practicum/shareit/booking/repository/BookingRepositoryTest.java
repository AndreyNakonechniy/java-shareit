package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    private BookingRepository bookingRepository;

    User owner = new User("user", "user@user.ru");
    User booker = new User("user2", "user@user2.ru");
    Item item = new Item("name", "description", true, owner);
    Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
    Booking booking2 = new Booking(LocalDateTime.now().plusHours(6), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);
    Booking booking3 = new Booking(LocalDateTime.now().plusHours(12), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);
    PageRequest defaultPageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        testEntityManager.persist(owner);
        testEntityManager.persist(booker);
        testEntityManager.persist(item);
        testEntityManager.flush();
        bookingRepository.save(booking);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    @Test
    void findAllByItemIdOrderByStart() {
        List<Booking> checkBooking = bookingRepository.findAllByItemIdOrderByStart(1L);

        assertEquals(3, checkBooking.size());
        assertEquals(2, checkBooking.get(0).getBooker().getId());
        assertEquals(3, checkBooking.get(2).getId());
    }

    @Test
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByBookerIdOrderByStartDesc(2L, defaultPageRequest);

        assertEquals(3, checkBooking.size());
        assertEquals(2, checkBooking.get(0).getBooker().getId());
        assertEquals(1, checkBooking.get(2).getId());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(2L, LocalDateTime.now().plusHours(12),
                LocalDateTime.now().plusDays(1), defaultPageRequest);

        assertEquals(1, checkBooking.size());
        assertEquals(2, checkBooking.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(2L, LocalDateTime.now().plusDays(2), defaultPageRequest);

        assertEquals(3, checkBooking.size());
        assertEquals(3, checkBooking.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(2L, LocalDateTime.now().plusHours(7), defaultPageRequest);

        assertEquals(1, checkBooking.size());
        assertEquals(3, checkBooking.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(2L, LocalDateTime.now(), BookingStatus.APPROVED, defaultPageRequest);

        assertEquals(2, checkBooking.size());
        assertEquals(2, checkBooking.get(1).getId());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING, defaultPageRequest);

        assertEquals(1, checkBooking.size());
        assertEquals(1, checkBooking.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(1L, defaultPageRequest);

        assertEquals(3, checkBooking.size());
        assertEquals(3, checkBooking.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(1L,
                LocalDateTime.now(), LocalDateTime.now().plusHours(12), defaultPageRequest);

        assertEquals(1, checkBooking.size());
        assertEquals(1, checkBooking.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(1L, LocalDateTime.now().plusDays(1), defaultPageRequest);

        assertEquals(2, checkBooking.size());
        assertEquals(3, checkBooking.get(0).getId());
        assertEquals(1, checkBooking.get(1).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(1L, LocalDateTime.now().minusHours(1), defaultPageRequest);

        assertEquals(3, checkBooking.size());
        assertEquals(3, checkBooking.get(0).getId());
        assertEquals(1, checkBooking.get(2).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(1L, LocalDateTime.now().minusHours(1), BookingStatus.WAITING, defaultPageRequest);

        assertEquals(1, checkBooking.size());
        assertEquals(1, checkBooking.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> checkBooking = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.APPROVED, defaultPageRequest);

        assertEquals(2, checkBooking.size());
        assertEquals(3, checkBooking.get(0).getId());
        assertEquals(2, checkBooking.get(1).getId());
    }
}