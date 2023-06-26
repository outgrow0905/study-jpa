package com.study.jpa.app.v4;

import com.study.jpa.app.v3.DeliveryV3;
import com.study.jpa.app.v3.OrderItemV3;
import com.study.jpa.app.v3.UserV3;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class OrderV4 extends BaseEntityV4 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @JoinColumn(name="USER_ID")
    @ManyToOne
    private UserV4 user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItemV4> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "DELIVERY_ID")
    DeliveryV4 delivery;

    public void setUser(UserV4 user) {
        if (this.user != null) {
            this.user.getOrders().remove(this);
        }

        this.user = user;
        this.user.getOrders().add(this);
    }

    public void addOrderItem(OrderItemV4 orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public void setDelivery(DeliveryV4 delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public enum OrderStatus {
        ORDER, CANCEL
    }
}