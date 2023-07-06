package com.study.jpa.ch6.v3;

import com.study.jpa.ch6.v4.MyChildV2;
import com.study.jpa.ch6.v4.MyParentV2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CascadeTest {


    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        template(
                manager -> {
                    manager.createQuery("DELETE FROM HelloMemberV1 m").executeUpdate();
                    manager.createQuery("DELETE FROM HelloTeamV1 t").executeUpdate();
                    manager.createQuery("DELETE FROM HelloMemberV2 m").executeUpdate();
                    manager.createQuery("DELETE FROM HelloTeamV2 t").executeUpdate();
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
    void withoutCascade() {
        template(manager -> {
            MyParentV1 parent = new MyParentV1();
            manager.persist(parent);

            MyChildV1 child1 = new MyChildV1();
            child1.setParent(parent);
            manager.persist(child1);

            MyChildV1 child2 = new MyChildV1();
            child2.setParent(parent);
            manager.persist(child2);
        });
    }

    @Test
    void cascadePersist() {
        template(manager -> {
            MyChildV2 child1 = new MyChildV2();
            MyChildV2 child2 = new MyChildV2();

            MyParentV2 parent = new MyParentV2();
            child1.setParent(parent); // 저장 안하면 child 테이블에 insert 할떄에 parent fk가 null로 저장된다.
            child2.setParent(parent);
            parent.setChildren(List.of(child1, child2));

            manager.persist(parent);
        });
    }

    @Test
    void cascadeRemove() {
        template(manager -> {
            MyChildV2 child1 = new MyChildV2();
            MyChildV2 child2 = new MyChildV2();

            MyParentV2 parent = new MyParentV2();
            child1.setParent(parent);
            child2.setParent(parent);
            parent.setChildren(List.of(child1, child2));

            // persist
            manager.persist(parent);

            // remove
            manager.remove(parent);
        });
    }
}