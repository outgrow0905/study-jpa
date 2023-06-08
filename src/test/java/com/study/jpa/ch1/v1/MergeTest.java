package com.study.jpa.ch1.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.lang.reflect.Member;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class MergeTest {
    EntityManagerFactory factory;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("jpabook");
    }

    @AfterEach
    void close() {
        factory.close();
    }

    @Test
    void persist() {
        // insert member
        template(manager -> {
            MemberV1 memberV1 = new MemberV1();
            memberV1.setId("id1");
            memberV1.setUsername("name1");
            memberV1.setAge(10);

            manager.persist(memberV1);

            return memberV1;
        });

        assertThrows(
                RollbackException.class,
                () -> template(manager -> {
                    MemberV1 memberV1 = new MemberV1();
                    memberV1.setId("id1");
                    memberV1.setUsername("name2");
                    memberV1.setAge(20);

                    // persist
                    manager.persist(memberV1);

                    return memberV1;
                }));
    }

    @Test
    void merge() {
        // insert member
        template(manager -> {
            MemberV1 memberV1 = new MemberV1();
            memberV1.setId("id1");
            memberV1.setUsername("name1");
            memberV1.setAge(10);

            manager.persist(memberV1);

            return memberV1;
        });

        template(manager -> {
            MemberV1 memberV1 = new MemberV1();
            memberV1.setId("id1");
            memberV1.setUsername("name2");
            memberV1.setAge(20);

            // merge
            MemberV1 mergedMember = manager.merge(memberV1);
            log.info("mergedMember: {}", mergedMember);

            return memberV1;
        });
    }


    @Test
    void persistAndPersist() {
        template(manager -> {
            // persist
            MemberV1 member1 = new MemberV1();
            member1.setId("id1");
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            // persist
            MemberV1 member2 = new MemberV1();
            member2.setId("id1");
            member2.setUsername("name2");
            assertThrows(EntityExistsException.class, () -> manager.persist(member2));

            return member2;
        });
    }

    @Test
    void persistAndMerge() {
        template(manager -> {
            // persist
            MemberV1 member1 = new MemberV1();
            member1.setId("id1");
            member1.setUsername("name1");
            member1.setAge(10);
            manager.persist(member1);

            // merge
            MemberV1 member2 = new MemberV1();
            member2.setId("id1");
            member2.setUsername("name2");
            manager.merge(member2);

            MemberV1 findMember = manager.find(MemberV1.class, "id1");
            log.info("findMember: {}", findMember);

            return member2;
        });
    }

    private MemberV1 template(Function<EntityManager, MemberV1> function) {
        EntityManager manager = factory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();

        MemberV1 result;
        try {
            transaction.begin();
            result = function.apply(manager);
            transaction.commit();
        } catch (Exception e) {
            log.error("{}", e);
            transaction.rollback();
            throw e;
        } finally {
            manager.close();
        }

        return result;
    }
}
