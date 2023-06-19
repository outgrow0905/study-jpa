package com.study.jpa.app.v3;

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
public class AppV3Test {
    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
    }

    @AfterEach
    void close() {
        factory.close();
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

    @Test
    void insertUser() {
        template((manager) -> {
            // insert delivery
            DeliveryV3 delivery1 = new DeliveryV3();
            delivery1.setCity("city1");
            delivery1.setZipcode("00001");
            delivery1.setStreet("street1");
            delivery1.setStatus(DeliveryV3.DeliveryStatus.READY);
            manager.persist(delivery1);

            // insert user
            UserV3 user1 = new UserV3();
            user1.setName("name1");
            user1.setCity("Seoul");
            user1.setStreet("street1");
            user1.setZipcode("00142");
            manager.persist(user1);

            // insert order
            OrderV3 order1 = new OrderV3();
            order1.setUser(user1); // 연관관계의 주인
            order1.setOrderDate(LocalDateTime.now());
            order1.setStatus(OrderV3.OrderStatus.ORDER);
            order1.setDelivery(delivery1); // 연관관계의 주인
            manager.persist(order1);

            // insert item
            ItemV3 item1 = new ItemV3();
            item1.setName("item name1");
            item1.setPrice(100);
            item1.setStockQuantity(10);
            manager.persist(item1);

            ItemV3 item2 = new ItemV3();
            item2.setName("item name2");
            item2.setPrice(200);
            item2.setStockQuantity(20);
            manager.persist(item2);

            // insert order-item
            OrderItemV3 orderItem1 = new OrderItemV3(item1, 2);
            order1.addOrderItem(orderItem1);
            manager.persist(orderItem1);

            OrderItemV3 orderItem2 = new OrderItemV3(item2, 2);
            order1.addOrderItem(orderItem2);
            manager.persist(orderItem2);

            // insert category
            CategoryV3 category1 = new CategoryV3();
            category1.setName("category1");
            category1.setItems(List.of(item1));  // 연관관계의 주인
            manager.persist(category1);

            CategoryV3 category2 = new CategoryV3();
            category2.setName("category2");
            category2.setItems(List.of(item1, item2)); // 연관관계의 주인
            manager.persist(category2);
        });

        template(manager -> {
            UserV3 user1 = manager.find(UserV3.class, 1L);

            // find order by member
            OrderV3 order1 = user1.getOrders().get(0);
            log.info("findOrder: {}", order1);

            // find delivery by order
            DeliveryV3 delivery = order1.getDelivery();
            log.info("delivery: {}", delivery);

            // find member by order
            UserV3 findUser = order1.getUser();
            log.info("findUser: {}", findUser);

            // find order-item by order
            List<OrderItemV3> findOrderItems = order1.getOrderItems();
            log.info("findOrderItems: {}", findOrderItems);

            // find item by order
            ItemV3 findItem = findOrderItems.get(0).getItem();
            log.info("findItem: {}", findItem);

            // find categories by item
            List<CategoryV3> categories = findItem.getCategories();
            log.info("categories: {}", categories);
        });
    }
}
