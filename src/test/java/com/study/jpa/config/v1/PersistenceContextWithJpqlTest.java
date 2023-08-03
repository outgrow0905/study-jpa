package com.study.jpa.config.v1;

import com.study.jpa.ch8.v1.CAddressV1;
import com.study.jpa.ch8.v1.CMemberV1;
import com.study.jpa.ch8.v1.COrderV1;
import com.study.jpa.ch8.v1.CProductV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PersistenceContextWithJpqlTest {
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
    void persistenceContext1() {
        template(manager -> {
            // member
            CMemberV1 member1 = new CMemberV1();
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            // product
            CProductV1 product1 = new CProductV1();
            product1.setName("product1");
            manager.persist(product1);

            // order
            COrderV1 order1 = new COrderV1();
            order1.setMember(member1);
            order1.setOrderAmount(100);
            order1.setProduct(product1);
            order1.setAddress(new CAddressV1("city1", "street1", "00000"));
            manager.persist(order1);
        });

        template(manager -> {
            CMemberV1 member = manager.createQuery(
                    "select m from CMemberV1 m",
                    CMemberV1.class
            ).getSingleResult();

            member.setUsername("name2"); // update 실행 (영속성 컨텍스트 관리 됨)
        });

        template(manager -> {
            CAddressV1 address1 = manager.createQuery(
                    "select o.address from COrderV1 o",
                    CAddressV1.class
            ).getSingleResult();
            address1.setStreet("street1"); // update 실행안됨 (영속성 컨텍스트 관리 됨)
        });

        template(manager -> {
            List members = manager.createQuery(
                    "select m.username, m.id from CMemberV1 m"
            ).getResultList(); // 단순 필드조회일 뿐이다. 키를 기준으로 관리하는 영속성 컨텍스트에 등록안된다.
        });

        template(manager -> {
            manager.createQuery("delete from COrderV1 o").executeUpdate();
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
            manager.createQuery("delete from CProductV1 p").executeUpdate();
        });
    }

    @Test
    void persistenceContext2() {
        CMemberV1 member1 = new CMemberV1();
        CMemberV1 member2 = new CMemberV1();
        template(manager -> {
            // member
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            member2.setUsername("name2");
            member2.setAge(20);
            manager.persist(member2);
        });

        template(manager -> {
            // member1 조회
            CMemberV1 findMember1 = manager.find(CMemberV1.class, member1.getId());

            // member1, member2 조회
            List<CMemberV1> members = manager.createQuery(
                    "select m from CMemberV1 m",
                    CMemberV1.class
            ).getResultList();
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }

    @Test
    void flushMode1() {
        CMemberV1 member1 = new CMemberV1();
        template(manager -> {
            // member
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);
        });

        template(manager -> {
            // member1 조회
            CMemberV1 findMember1 = manager.find(CMemberV1.class, member1.getId());
            findMember1.setAge(20);

            // member1 jpql 조회
            CMemberV1 findMemberByJpql = manager.createQuery(
                    "select m from CMemberV1 m",
                    CMemberV1.class
            ).getSingleResult();
            log.info("findMemberByJpql age: {}", findMemberByJpql.getAge());
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }

    @Test
    void flushMode2() {
        CMemberV1 member1 = new CMemberV1();
        template(manager -> {
            // member
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);
        });

        template(manager -> {
            manager.setFlushMode(FlushModeType.COMMIT);

            // member1 조회
            CMemberV1 findMember1 = manager.find(CMemberV1.class, member1.getId());
            findMember1.setAge(20); // 10 -> 20

            // member1 jpql 조회
            assertThrows(
                NoResultException.class,
                () -> {
                    manager.createQuery(
                            "select m from CMemberV1 m where m.age = 20",
                            CMemberV1.class
                    ).getSingleResult();
                });
        });

        template(manager -> {
            manager.createQuery("delete from CMemberV1 m").executeUpdate();
        });
    }
}
