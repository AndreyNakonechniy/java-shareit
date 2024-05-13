package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    TestEntityManager testEntityManager;

    User user = new User("user", "email@email.ru");
    User itemOwner = new User("itemOwner", "email@email.com");
    Item item = new Item("name", "description", true, itemOwner, 1L);
    ItemRequest itemRequest = new ItemRequest("description", user, LocalDateTime.now());
    PageRequest defaultPageRequest = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        testEntityManager.persist(user);
        itemRequestRepository.save(itemRequest);
        testEntityManager.persist(itemOwner);
        testEntityManager.persist(item);
        testEntityManager.flush();
    }

    @Test
    void findByRequesterId() {
        List<ItemRequest> checkItemRequest = itemRequestRepository.findByRequesterId(1L, defaultPageRequest);

        assertEquals(1, checkItemRequest.size());
        assertEquals("description", checkItemRequest.get(0).getDescription());
    }

    @Test
    void findByItemsOwnerId() {
        List<ItemRequest> checkItemRequest = itemRequestRepository.findByItemsOwnerId(2L, defaultPageRequest);

        assertEquals(1, checkItemRequest.size());
        assertEquals("description", checkItemRequest.get(0).getDescription());
    }
}