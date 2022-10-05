package domain;

import status.DeliveryStatus;
import lombok.Data;

import javax.persistence.*;


@Entity
@Data
public class Delivery {

    @Id
    @GeneratedValue()
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
