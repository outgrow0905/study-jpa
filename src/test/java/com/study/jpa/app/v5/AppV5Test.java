package com.study.jpa.app.v5;

import com.study.jpa.app.v3.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class AppV5Test {
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
    void cascade() {
        template(manager -> {
            OrderV5 order = new OrderV5();
            DeliveryV5 delivery = new DeliveryV5();

            delivery.setOrder(order);
            order.setDelivery(delivery);

            // persist with cascade
//            manager.persist(delivery);
            manager.persist(order);
        });
    }

    @Test
    void lazyLoad() {
        final OrderV5 order = new OrderV5();
        order.setOrderDate(LocalDateTime.now());

        final DeliveryV5 delivery = new DeliveryV5();
        delivery.setZipcode("12345");

        template(manager -> {
            delivery.setOrder(order);
            order.setDelivery(delivery);

            // persist with cascade
            manager.persist(order);
        });

        template(manager -> {
            DeliveryV5 findDelivery = manager.getReference(DeliveryV5.class, delivery.getId());
            assertEquals("12345", findDelivery.getZipcode());
        });
    }
}
