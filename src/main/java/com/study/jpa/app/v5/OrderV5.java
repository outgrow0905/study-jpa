package com.study.jpa.app.v5;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class OrderV5 extends BaseEntityV5 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @JoinColumn(name="USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserV5 user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemV5> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "DELIVERY_ID")
    DeliveryV5 delivery;

    public void setUser(UserV5 user) {
        if (this.user != null) {
            this.user.getOrders().remove(this);
        }

        this.user = user;
        this.user.getOrders().add(this);
    }

    public void addOrderItem(OrderItemV5 orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public void setDelivery(DeliveryV5 delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public enum OrderStatus {
        ORDER, CANCEL
    }
}