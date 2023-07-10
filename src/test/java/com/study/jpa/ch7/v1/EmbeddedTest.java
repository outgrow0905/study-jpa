package com.study.jpa.ch7.v1;

import com.study.jpa.ch7.v2.AMemberV2;
import com.study.jpa.ch7.v3.AMemberV3;
import com.study.jpa.ch7.v3.Address;
import com.study.jpa.ch7.v4.AMemberV4;
import com.study.jpa.ch7.v4.ZipCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class EmbeddedTest {
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
    void embedded1() {
        template(manager -> {
            AMemberV3 member1 = new AMemberV3();
            Address address1 = new Address();
            address1.setCity("city1");
            member1.setHomeAddress(address1);
            manager.persist(member1);

            AMemberV3 member2 = new AMemberV3();
            Address address2 = member1.getHomeAddress();
            address2.setCity("city2");
            member2.setHomeAddress(address2);
            manager.persist(member2);
        });
    }

    @Test
    void embedded2() {
        template(manager -> {
            AMemberV4 member1 = new AMemberV4();
            com.study.jpa.ch7.v4.Address address1 = new com.study.jpa.ch7.v4.Address("city1", "street1", null);
            member1.setHomeAddress(address1);
            manager.persist(member1);

            AMemberV4 member2 = new AMemberV4();
            com.study.jpa.ch7.v4.Address address2 =
                    new com.study.jpa.ch7.v4.Address(member1.getHomeAddress().getCity(), member1.getHomeAddress().getStreet(), member1.getHomeAddress().getZipCode());
            member2.setHomeAddress(address2);
            manager.persist(member2);
        });
    }
}