package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    public List<ItemRequest> findByRequesterId(Long id, Pageable pageable);

    public List<ItemRequest> findByItemsOwnerId(Long id, Pageable pageable);
}