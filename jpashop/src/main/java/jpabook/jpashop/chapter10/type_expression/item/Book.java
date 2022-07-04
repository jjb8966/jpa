package jpabook.jpashop.chapter10.type_expression.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter @Setter
public class Book extends Item {

    private String author;
    private String isbn;
}
