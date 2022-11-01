package domain.item;

import domain.Category;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "what_kind_of_item")
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    protected Long id;

    protected String name;
    protected int price;
}
