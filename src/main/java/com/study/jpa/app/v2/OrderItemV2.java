package com.study.jpa.app.v2;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OrderItemV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @JoinColumn(name = "ITEM_ID")
    @ManyToOne
    private ItemV2 item;

    @JoinColumn(name = "ORDER_ID")
    @ManyToOne
    private OrderV2 order;

    private int orderPrice;

    private int count;

    public OrderItemV2(ItemV2 item, int count) {
        this.item = item;
        this.count = count;
        this.orderPrice = item.getPrice() * count;
    }
}
