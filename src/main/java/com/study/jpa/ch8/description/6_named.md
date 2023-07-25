#### 정적쿼리
지금까지 배운 jpql은 모두 `동적쿼리`이다.  
런타임에서 특정조건에 따라 쿼리가 만들어지고 수행된다는 의미이기도 하다.  
jpql에서는 특정 쿼리를 미리 만들어두고, 어플리케이션이 올라가는 시점에 미리 파싱(문법오류 체크)과 캐싱을 할 수 있는 기능이 있다.  
미리 파싱을 해두기 때문에 실제 사용시점에 성능상 이점이 있기도 하다.  
이를 `정적쿼리`라고 하기도 한다.


#### @NamedQuery
`정적쿼리`는 `@NamedQuery` 어노테이션을 사용한다.  
아래와 같이 엔티티에 적어두면 된다. 실제 사용은 테스트코드를 참고하자.
~~~java
@Entity
@NamedQuery(
        name = "CMemberV2.findByUsername", query = "select m from CMemberV2 m where m.username = :username"
)
public class CMemberV2 {
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private int age;
}
~~~

~~~java
@Test
void namedQuery() {
    template(manager -> {
        CMemberV2 member1 = new CMemberV2();
        member1.setUsername("name1");
        member1.setAge(10);
        manager.persist(member1);
    
        CMemberV2 member2 = new CMemberV2();
        member2.setUsername("name2");
        member2.setAge(20);
        manager.persist(member2);
    });

    template(manager -> {
        List<CMemberV2> members =
            manager.createNamedQuery("CMemberV2.findByUsername", CMemberV2.class)
            .setParameter("username", "name1")
            .getResultList();

        assertEquals(1, members.size());
    });
}
~~~