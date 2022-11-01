package domain.item;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@Data
@DiscriminatorValue("this_is_book")
@NamedQuery(name = "Book.findByName",
            query = "select b from Book b where b.name = :name")
public class Book extends Item {

    private String author;
    private String isbn;

    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
