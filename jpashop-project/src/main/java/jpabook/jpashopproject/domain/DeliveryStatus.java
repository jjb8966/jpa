package jpabook.jpashopproject.domain;

import javax.persistence.Embeddable;

@Embeddable
public enum DeliveryStatus {
    READY, COMP
}
