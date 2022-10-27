package domain.item;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@DiscriminatorValue("this_is_book")
public class Book extends Item {

    private String author;
    private String isbn;
}
