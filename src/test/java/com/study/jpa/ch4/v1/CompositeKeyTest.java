package com.study.jpa.ch4.v1;

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
class CompositeKeyTest {
    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        template(
                manager -> {
                    manager.createQuery("DELETE FROM ChildV1 c").executeUpdate();
                    manager.createQuery("DELETE FROM ParentV1 p").executeUpdate();
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
    void idClass() {
        template(manager -> {
            // insert
            ParentV1 parent1 = new ParentV1();
            parent1.setParentId1(1);
            parent1.setParentId2(1);
            parent1.setName("parent1");
            manager.persist(parent1);
        });

        template(manager -> {
            // select
            ParentV1Id parentId1 = new ParentV1Id(1, 1);
            ParentV1 parent1 = manager.find(ParentV1.class, parentId1);
            assertEquals("parent1", parent1.getName());
        });
    }

    @Test
    void idClassChild() {
        template(manager -> {
            // insert parent
            ParentV1 parent1 = new ParentV1();
            parent1.setParentId1(1);
            parent1.setParentId2(1);
            parent1.setName("parent1");
            manager.persist(parent1);

            // insert child
            ChildV1 child1 = new ChildV1();
            child1.setParent(parent1);
            manager.persist(child1);
            log.info("hello: {}", child1.getChildId1());
        });

        template(manager -> {
            // select
            ParentV1Id parentId1 = new ParentV1Id(1, 1);
            ParentV1 parent1 = manager.find(ParentV1.class, parentId1);
            assertEquals("parent1", parent1.getName());
        });
    }
}