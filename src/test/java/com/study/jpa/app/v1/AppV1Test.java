package com.study.jpa.app.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AppV1Test {
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
            UserV1 userV1 = new UserV1();
            userV1.setName("name1");
            userV1.setCity("Seoul");
            userV1.setStreet("street1");
            userV1.setZipcode("00142");
            manager.persist(userV1);

            // insert order
            OrderV1 orderV1 = new OrderV1();
            orderV1.setUserId(userV1.getId());
            orderV1.setOrderDate(LocalDateTime.now());
            orderV1.setStatus(OrderV1.OrderStatus.ORDER);
            manager.persist(orderV1);

            // select user
            UserV1 findUser = manager.find(UserV1.class, orderV1.getUserId());
//            UserV1 findUser = orderV1.getUserV1();
            log.info("findUser: {}", findUser);
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