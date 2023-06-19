package com.study.jpa.app.v3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OrderItemV3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @JoinColumn(name = "ITEM_ID")
    @ManyToOne
    private ItemV3 item;

    @JoinColumn(name = "ORDER_ID")
    @ManyToOne
    private OrderV3 order;

    private int orderPrice;

    private int count;

    public OrderItemV3(ItemV3 item, int count) {
        this.item = item;
        this.count = count;
        this.orderPrice = item.getPrice() * count;
    }
}
