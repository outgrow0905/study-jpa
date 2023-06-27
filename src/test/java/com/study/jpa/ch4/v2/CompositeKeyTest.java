package com.study.jpa.ch4.v2;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class CompositeKeyTest {
    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        template(
                manager -> {
                    manager.createQuery("DELETE FROM ChildV2 c").executeUpdate();
                    manager.createQuery("DELETE FROM ParentV2 p").executeUpdate();
                }
        );
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
    void embeddedId() {
        template(manager -> {
            // insert
            ParentV2 parent1 = new ParentV2();
            parent1.setId(new ParentV2Id(1, 1));
            parent1.setName("parent1");
            manager.persist(parent1);
        });

        template(manager -> {
            // select
            ParentV2Id parentId1 = new ParentV2Id(1, 1);
            ParentV2 parent1 = manager.find(ParentV2.class, parentId1);
            assertEquals("parent1", parent1.getName());
        });
    }

    @Test
    void embeddedIdChild() {
        template(manager -> {
            // insert parent
            ParentV2 parent1 = new ParentV2();
            parent1.setId(new ParentV2Id(1, 1));
            parent1.setName("parent1");
            manager.persist(parent1);

            // insert child
            ChildV2 child1 = new ChildV2();
            child1.setParent(parent1);
            manager.persist(child1);

            assertEquals(parent1, child1.getParent());
        });
    }

    @Test
    void equalsAndHashCode() {
        ParentV2Id id1 = new ParentV2Id(1, 1);
        ParentV2Id id2 = new ParentV2Id(1, 1);
        log.info("equals: {}", id1.equals(id2));
    }
}