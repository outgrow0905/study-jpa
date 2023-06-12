package com.study.jpa.app.v2;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class OrderV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @JoinColumn(name="USER_ID")
    @ManyToOne
    private UserV2 user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItemV2> orderItems = new ArrayList<>();

    public void setUser(UserV2 user) {
        if (this.user != null) {
            this.user.getOrders().remove(this);
        }

        this.user = user;
        this.user.getOrders().add(this);
    }

    public void addOrderItem(OrderItemV2 orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public enum OrderStatus {
        ORDER, CANCEL
    }
}