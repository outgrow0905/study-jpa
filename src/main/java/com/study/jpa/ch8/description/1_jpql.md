#### jpql
이전에 잠깐 사용해보았던 jpql을 알아보자.  
jpql은 처음보면 마치 일반 sql과 거의 똑같다. 그러나 일반 sql과 동일하게 사용하면 오류가 발생할 수 있기 때문에 미리 jpql의 특징을 알아야 한다.  

##### 특징
jpql은 객체지향 쿼리이다.  
데이터베이스의 테이블을 대상으로 질의하는 것이 아니라 엔티티를 대상으로 질의한다.  
`company_member` 테이블의 엔티티가 `Member`이라면, `SELECT m FROM company_member m`이 아니라 `select m from Member m`이 되어야 한다.   

객체지향 쿼리의 다른의미는 특정 데이터베이스에 의존하지 않는다는 말이기도 하다.  
엔티티를 대상으로 질의하기 때문에 어떤 데이터베이스를 사용하는지 몰라도 된다.

결국 위 특징들의 의미는 jpql은 결국 쿼리릎 파싱하여 진짜 sql을 다시 생성하는 일을 수행한다는 것이다.  

간단한 예제로 실습해보자.  

~~~java
@Test
void jpql1() {
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
}
~~~

만약 `select m`이 아닌 `select m.id, m.username`이라면 어떻게 될까?  
`CMemberV1`으로 변환할 수 없을 것이다.  
그렇다면 어쩔수 없이 리턴타입을 명시할 수 없고 `Object`로 받아야 한다.  
코드는 아래와 같을 것이다.

~~~java
@Test
void jpql2() {
    template(manager -> {
        List resultList = (List<CMemberV1>)manager.createQuery(
                "select m.id, m.username from CMemberV1 m"
        ).getResultList();

        resultList.forEach(object -> {
            Object[] member = (Object[])object;
            log.info("id: {}", member[0]);
            log.info("name: {}", member[1]);
        });
    });
}
~~~

결과값이 무조건 하나만 나와야 한다면 `getResultList()` 대신에 `getSingleResult()`를 사용할 수도 있다.
`getSingleResult()` 사용시 주의할 점은 결과값이 없거나 2개 이상일 경우에 오류가 발생한다는 것이다.  
아래의 테스트코드를 참고하자.

~~~java
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
}
~~~

간단한 파라미터 바인딩도 진행해보자.  

~~~java
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
~~~

#### 임베디드 타입 조회
임베디드 타입을 조회할 떄에는 주의할 점이 있다.  
임베디드는 엔티티관리를 객체지향적으로 하기 위해 사용하는 용도일뿐 엔티티는 아니다.  
따라서 엔티티를 기준으로 조회하는 jpql에서 임베디드 타입 그자체는 쿼리의 대상이 될 수 없다.  
`from`뒤에 올 수 없다는 의미이기도 하다.    
임베디드는 조회의 시작점이 될 수 없고 엔티티로부터 조회를 해야한다.      
아래의 예시를 보고 빠르게 넘어가자.

`address`는 임베디드이고 엔티티가 아니기 때문에 `from Address`와 같이 조회할 수 없고,  
`address`가 속한 `order`로부터 `o.address`와 같이 조회해야 한다.

~~~java
@Test
void jpql5() {
    template(manager -> {
        CAddressV1 address = manager.createQuery(
            "select o.address from COrderV1 o",
            CAddressV1.class
        ).getSingleResult();
    
        assertEquals("city1", address.getCity());
    });
}
~~~


#### 페이징
페이징을 처리하는것은 번거로운 일이다.  
데이터베이스마다 페이징을 처리하는 방법이 다 다르기 때문이다.  
예를 들어, 어떤 데이터베이스는 `0`부터 페이징이 시작하고, 어떤 데이터베이스는 `1`부터 페이징을 시작한다.  

jpa에서 페이징을 어떻게 처리하는지 아래에서 알아보자.  
`setFirstResult(), setMaxResults()`을 통해서 처리한다.   
`setFirstResult()`는 어디서부터 조회할지를 의미하며 0부터 시작한다.  
`setMaxResults()`는 몇개의 값을 가져올지 최대값을 의미한다.  

예를 들어, `setFirstResult(2).setMaxResults(1)`를 설정하면 `3`번째데이터 `1`개를 조회하게 된다.

~~~java
@Test
void paging() {
    template(manager -> {
        CMemberV1 member1 = new CMemberV1();
        member1.setUsername("name1");
        member1.setAge(10);
        manager.persist(member1);

        CMemberV1 member2 = new CMemberV1();
        member2.setUsername("name2");
        member2.setAge(20);
        manager.persist(member2);

        CMemberV1 member3 = new CMemberV1();
        member3.setUsername("name3");
        member3.setAge(30);
        manager.persist(member3);

        CMemberV1 member4 = new CMemberV1();
        member4.setUsername("name4");
        member4.setAge(40);
        manager.persist(member4);
    });

    template(manager -> {
        CMemberV1 member = manager.createQuery(
                "select m from CMemberV1 m",
                CMemberV1.class)
                .setFirstResult(2)
                .setMaxResults(1)
                .getSingleResult();

        assertEquals("name3", member.getUsername());
    });
}
~~~