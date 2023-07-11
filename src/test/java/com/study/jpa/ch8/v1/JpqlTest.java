package com.study.jpa.ch8.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JpqlTest {
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
    void jpql1() {
        template(manager -> {
            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);
        });

        template(manager -> {
            List<CMemberV1> members = manager.createQuery(
                    "select m from CMemberV1 m",
                    CMemberV1.class
            ).getResultList();

            members.forEach(member -> {
                log.info("id: {}", member.getId());
                log.info("name: {}", member.getUsername());
                log.info("age: {}", member.getAge());
            });
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }

    @Test
    void jpql2() {
        template(manager -> {
            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);
        });

        template(manager -> {
            List resultList = (List<CMemberV1>)manager.createQuery(
                    "select m.id, m.username, m.age from CMemberV1 m"
            ).getResultList();

            resultList.forEach(object -> {
                Object[] member = (Object[])object;
                log.info("id: {}", member[0]);
                log.info("name: {}", member[1]);
                log.info("age: {}", member[2]);
            });
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }

    @Test
    void jpql3() {
        template(manager -> {
            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            CMemberV1 member2 = new CMemberV1();
            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);
        });

        template(manager -> {
            assertThrows(NonUniqueResultException.class,
                    () ->
                    manager.createQuery(
                    "select m from CMemberV1 m",
                    CMemberV1.class
            ).getSingleResult());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }

    @Test
    void jpql4() {
        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });

        template(manager -> {
            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);
        });

        template(manager -> {
            CMemberV1 member = manager.createQuery(
                            "select m from CMemberV1 m where m.username = :username",
                            CMemberV1.class)
                    .setParameter("username", "name1")
                    .getSingleResult();

            assertEquals("name1", member.getUsername());
        });
    }
}