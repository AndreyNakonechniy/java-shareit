package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIdOrderByStart(Long id);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long id, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long id, LocalDateTime one, LocalDateTime two, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(Long id, LocalDateTime time, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long id, LocalDateTime one, LocalDateTime two, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(Long id, LocalDateTime time, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long id, BookingStatus status, PageRequest pageRequest);


}
