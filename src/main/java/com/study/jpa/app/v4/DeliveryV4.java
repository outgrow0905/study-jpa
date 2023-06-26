package com.study.jpa.app.v4;

import com.study.jpa.app.v3.OrderV3;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class DeliveryV4 extends BaseEntityV4 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_ID")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    OrderV4 order;

    private String city;
    private String street;
    private String zipcode;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public enum DeliveryStatus {
        READY, COMPLETE
    }
}
