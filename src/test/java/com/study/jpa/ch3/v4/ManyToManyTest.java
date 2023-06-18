package com.study.jpa.ch3.v4;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ManyToManyTest {
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
    void insert1() {
        template(manager -> {
            MyProductV1 product1 = new MyProductV1();
            product1.setProductName("product name1");
            manager.persist(product1);

            MyOrderV1 order1 = new MyOrderV1();
            order1.setOrderName("order name1");
            order1.setProducts(List.of(product1));
            manager.persist(order1);
        });

        template(manager -> {
            MyProductV1 product1 = manager.find(MyProductV1.class, 1);
            MyOrderV1 order1 = manager.find(MyOrderV1.class, 1);

            assertEquals(product1.getOrders().get(0), order1);
            assertEquals(order1.getProducts().get(0), product1);
        });

    }
}
