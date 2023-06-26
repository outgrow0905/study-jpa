package com.study.jpa.app.v4;

import com.study.jpa.app.v3.ItemV3;
import com.study.jpa.app.v3.OrderV3;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OrderItemV4 extends BaseEntityV4 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @JoinColumn(name = "ITEM_ID")
    @ManyToOne
    private ItemV4 item;

    @JoinColumn(name = "ORDER_ID")
    @ManyToOne
    private OrderV4 order;

    private int orderPrice;

    private int count;

    public OrderItemV4(ItemV4 item, int count) {
        this.item = item;
        this.count = count;
        this.orderPrice = item.getPrice() * count;
    }
}
