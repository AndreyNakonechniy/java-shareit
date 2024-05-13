package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIdOrderByStart(Long id);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long id, LocalDateTime one, LocalDateTime two, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(Long id, LocalDateTime time, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long id, LocalDateTime one, LocalDateTime two, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(Long id, LocalDateTime time, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long id, BookingStatus status, Pageable pageable);


}
