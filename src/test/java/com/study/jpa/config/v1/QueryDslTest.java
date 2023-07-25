package com.study.jpa.config.v1;

import com.querydsl.jpa.impl.JPAQuery;
import com.study.jpa.ch9.v1.DMemberV1;
import com.study.jpa.ch9.v1.QDMemberV1;
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

@Slf4j
class QueryDslTest {
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
    void queryDsl1() {
        template(manager -> {
            JPAQuery<DMemberV1> query = new JPAQuery<>(manager);
            QDMemberV1 member1 = new QDMemberV1("m");
            List<DMemberV1> members = query.from(member1)
                    .where(member1.username.eq("name1"))
                    .orderBy(member1.username.desc())
                    .stream().toList();
        });
    }
}