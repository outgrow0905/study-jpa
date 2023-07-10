#### ElementCollection
값 타임을 컬렉션에 보관하고자 한다면 `@ElementCollection`을 사용해야 한다.  
예를 들어, `회원`과 `선호음식`의 관계를 생각해보자.  
`회원`의 입장에서는 `일대다` 관계이고, 테이블 설계 관점에서도 두개의 테이블이 필요하다.  
이를 `@ElementCollection`을 이용해서 구현해보자.

~~~java
@Entity
public class BMemberV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection
    @CollectionTable(
            name = "FAVORITE_FOODS",
            joinColumns = @JoinColumn(name = "MEMBER_ID"))
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();
}
~~~

정리는 하였으나....
`@Embeddable` 참조는 엔티티를 객체지향적으로 관리할 수 있어서 편리한 점이 분명히 있다.  
하지만, `@ElementCollection`는 사용하지 말자.  
러닝커브만 있고 별로 이득이 없을뿐더러 엔티티 수정 시 `update`가 아닌` delete, insert`가 수행된다. `@ManyToOne`을 사용하자.  