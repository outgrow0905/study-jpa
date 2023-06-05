package com.study.jpa.ch1.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberV1Test {

    @Test
    void helloJpa() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("jpabook");
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        try {
            transaction.begin();
            logic(manager);
            transaction.commit();
        } catch (Exception e) {
            log.error("{}", e);
            transaction.rollback();
        } finally {
            manager.close();
        }
        factory.close();

    }

    private static void logic(EntityManager manager) {
        MemberV1 member = new MemberV1();
        member.setId("id1");
        member.setUsername("name1");
        member.setAge(20);

        // insert
        manager.persist(member);

        // select
        MemberV1 findMember = manager.find(MemberV1.class, "id1");
        log.info("member1: {}", findMember);

        // update
        member.setAge(21);

        // select
        findMember = manager.find(MemberV1.class, "id1");
        log.info("member2: {}", findMember);

        // select list
        List<MemberV1> memberV1List = manager.createQuery("SELECT m FROM MemberV1 m", MemberV1.class).getResultList();
        log.info("member list: {}", memberV1List);

        // delete
        manager.remove(member);
    }
}