package jpabook.jpashopproject.service;

import jpabook.jpashopproject.domain.item.Book;
import jpabook.jpashopproject.domain.item.Item;
import jpabook.jpashopproject.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity, String author, String isbn) {
        // 영속성 컨텍스트가 관리 -> 변경 감지 O
        Book findItem = (Book) itemRepository.findOne(id);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        findItem.setAuthor(author);
        findItem.setIsbn(isbn);

        // 트랜잭션이 끝나면 자동으로 커밋
    }
}
