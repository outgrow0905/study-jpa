package com.study.jpa.app.v2;

import com.study.jpa.ch2.v1.MemberV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
class AppV2Test {
    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
    }

    @AfterEach
    void close() {
        factory.close();
    }

    @Test
    void insertUser() {
        template((manager) -> {
            // insert user
            UserV2 user1 = new UserV2();
            user1.setName("name1");
            user1.setCity("Seoul");
            user1.setStreet("street1");
            user1.setZipcode("00142");
            manager.persist(user1);

            // insert order
            OrderV2 order1 = new OrderV2();
            order1.setUser(user1);
            order1.setOrderDate(LocalDateTime.now());
            order1.setStatus(OrderV2.OrderStatus.ORDER);
            manager.persist(order1);

            // insert item
            ItemV2 item1 = new ItemV2();
            item1.setName("item name1");
            item1.setPrice(100);
            item1.setStockQuantity(10);
            manager.persist(item1);

            ItemV2 item2 = new ItemV2();
            item2.setName("item name2");
            item2.setPrice(200);
            item2.setStockQuantity(20);
            manager.persist(item2);

            // insert order-item
            OrderItemV2 orderItem1 = new OrderItemV2(item1, 2);
            order1.addOrderItem(orderItem1);
            manager.persist(orderItem1);

            OrderItemV2 orderItem2 = new OrderItemV2(item2, 2);
            order1.addOrderItem(orderItem2);
            manager.persist(orderItem2);
        });

        template(manager -> {
            UserV2 user1 = manager.find(UserV2.class, 1L);

            // find order by member
            OrderV2 order1 = user1.getOrders().get(0);
            log.info("findOrder: {}", order1);

            // find member by order
            UserV2 findUser = order1.getUser();
            log.info("findUser: {}", findUser);

            // find order-item by order
            List<OrderItemV2> findOrderItems = order1.getOrderItems();
            log.info("findOrderItems: {}", findOrderItems);

            // find item by order
            ItemV2 findItem = findOrderItems.get(0).getItem();
            log.info("findItem: {}", findItem);
        });
    }

    private void template(Consumer<EntityManager> consumer) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        try {
            transaction.begin();
            consumer.accept(manager);
            transaction.commit();
        } catch (Exception e) {
            log.error("{}", e);
            transaction.rollback();
            throw e;
        } finally {
            manager.close();
        }
    }
}