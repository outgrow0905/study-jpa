#### Hello JPA
JPA 시작하기 코드를 작성해보자.  
가장 먼저 필요한 것은 JPA 라이브러리를 사용하기 위한 의존성 설정일 것이다.  
이 프로젝트는 아래의 버전으로 진행한다.

~~~
implementation 'org.hibernate:hibernate-entitymanager:5.6.15.Final'
~~~

JPA를 연습하면서 쿼리를 한줄도 사용하지 않겠다는 마음가짐으로 시작하곘다.  
연습을 위해 생성할 객체는 아래와 같다.

~~~java
@Data
@Entity
public class MemberV1 {
    @Id
    @Column(name = "IO")
    private String id;

    @Column(name = "NAME")
    private String username;

    private Integer age;
}
~~~

`@Entity` 어노테이션을 통해 JPA는 이 객체가 데이터베이스와 연동된 객체라는 것을 인지한다.  
`MemberV1` 객체가 데이터베이스와 연동된 것은 알겠는데, 어떤 테이블이랑 연동되는지는 알 수 없다.  
별도 명시를 하지 않으면 클래스명과 동일하다고 간주한다.  
여기서는 `MemberV1` 테이블이 있다고 간주하게 된다.

`@Id` 어노테이션은 PK를 의미한다.  

`@Column` 어노테이션은 실제 테이블의 컬럼명과 객체의 변수의 이름이 다를 경우 `name`을 통해 명시한다.
데이터베이스 테이블에서는 `NAME`이고 코드에서는 `username`으로 사용하고자 한다면 위와 같이 설정하면 된다.  

없을 경우, 테이블의 컬럼명과 객체의 변수명이 같다고 간주한다.  
위의 예시에 `age`가 그 예시이다.



#### exercise
실제 코드로 DML을 수행해보자.   

~~~java
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
~~~

순서대로 살펴보자.   

`EntityManagerFactory` 객체는 데이터베이스 단위로 설정하는 객체이다.  
생성비용이 매우 크므로 어플리케이션이 실행되는 시점에 한번 생성하여 어플리케이션을 종료할때까지 재사용하는 것이 일반적이다.   

대부분 사용은 `EntityManager`를 통해서 한다.  
트렌젝션을 `EntityManager`에서 받아오고 사용이 끝나면 반환한다.  



#### JPQL
예시에서 `select list` 부분은 `JPQL`을 사용한 부분이다.  
JPA는 모든 작업을 객체를 통해서한다. 특정 조건으로 객체를 검색할 때에도 마찬가지이다.  
그러나 객체를 대상으로 검색을 해야한다면, `MemberV1` 테이블의 모든 데이터를 메모리에 올리고 질의를 해야하는 것일까?

예시를 들어보자.  
`@Table` 어노테이션을 통해 실제 테이블명은 Member 이고, 객체를 MemberV1으로 했다고 가정하자.

`JPQL`은 `SQL`과 유사하여 실제 데이터베이스에 질의하는 것처럼 보일 수 있다.
실제 `SQL`이었다면 `FROM` 부분에 `MemberV1`이 아닌 `Member`를 써야 할 것이다.   
하지만 `JPQL`에서는 `FROM` 부분에서 `Member`가 아닌, `MemberV1` 을 써야 한다.     
`JPQL`은 데이터베이스 정보는 알지도 못한다. `JPQL`을 통해 객체를 기준으로 질의하면 역시 쿼리를 만들어서 질의해주는 역할을 한다.  
뒤에서 자세히 알아보자.