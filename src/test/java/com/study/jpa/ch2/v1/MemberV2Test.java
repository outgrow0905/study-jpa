package com.study.jpa.ch2.v1;

import com.study.jpa.ch2.v1.MemberV2;
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

@Slf4j
class MemberV2Test {

    EntityManagerFactory factory;

//    @BeforeEach
//    void init() {
//        factory = Persistence.createEntityManagerFactory("jpabook");
//    }

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
        template(
                manager -> {
                    manager.createQuery("DELETE FROM MemberV2 m").executeUpdate();
                }
        );
    }

    @AfterEach
    void close() {
        factory.close();
    }


    @Test
    void insert() {
        template((manager) -> {
            MemberV2 memberV2 = new MemberV2();
            memberV2.setId("id1");
            memberV2.setUsername("name1");
            memberV2.setAge(10);
            memberV2.setCreateDate(LocalDateTime.now());
            memberV2.setLastModifiedDate(LocalDateTime.now());
            memberV2.setDescription("desc1");
            memberV2.setRoleType(MemberV2.RoleType.ADMIN);
            manager.persist(memberV2);

            MemberV2 findMember = manager.find(MemberV2.class, "id1");
            log.info("findMember: {}", findMember);
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