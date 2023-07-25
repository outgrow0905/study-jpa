package com.study.jpa.ch8.v1;

import com.study.jpa.ch8.v2.CMemberV2;
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
public class NamedTest {
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
    void namedQuery() {
        template(manager -> {
            CMemberV2 member1 = new CMemberV2();
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV2 member2 = new CMemberV2();
            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);
        });

        template(manager -> {
            List<CMemberV2> members =
                    manager.createNamedQuery("CMemberV2.findByUsername", CMemberV2.class)
                            .setParameter("username", "name1")
                            .getResultList();

            assertEquals(1, members.size());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV2").executeUpdate();
        });
    }
}
