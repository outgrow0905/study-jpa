#### 상속
`데이터베이스 설계`에서는 상속이라는 개념이 없다.  
그러나 비슷하게 구현할 수 있는 몇가지 방법이 있고 이에따라 JPA에서는 어떻게 구현하는 지 알아보자.  

하나의 `아이템(Item)`은 `이름, 가격`이라는 공통속성을 가지고 있고, `책, 영화, 앨범` 세가지로 분류될 수 있다.    
`책(Book)`은 `작가, isbn`을 가지고 있고, `영화(Movie)`는 `감독, 배우` 필드를 가지고 있으며, `앨범(Album)`은 `가수` 필드를 가지고 있다고 가정해보자.  

이러한 예시를 몇가지 `데이터베이스 설계`로 구현해보고 JPA에도 연동해보자.  



#### 조인전략 (JOINED)
이 전략에서는 각각의 테이블을 만드는 구조이다.  
`이름, 가격`의 공통속성을 가지고 있는 `아이템` 테이블 하나, `책` 테이블, `영화` 테이블, `앨범` 테이블 총 `4`개이다.  
이러한 설계의 장점은 정규화가 잘 되어서 저장공간을 효율적으로 사용할 수 있다는 점이다.  
단점은 조회시에 항상 조인을 사용해야 하고, 데이터 추가, 수정시에도 항상 두 테이블을 같이 추가, 수정해야한다는 번거로움이 있다.  
단순한 번거로움을 넘어 성능상의 포기해야하는 부분도 있는 것이다.  

조인전략 jpa 구현은 아래와 같다.

~~~java
@Entity
@DiscriminatorColumn(name = "DTYPE")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MyItemV1 {
    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
}

@Entity
@DiscriminatorValue("A")
public class MyAlbumV1 extends MyItemV1 {
    private String artist;
}
~~~

`조인전략`에는 `부모`의 역할을 하는 테이블 (ex. `Item`)에 `구분컬럼`이 들어있어야 한다.  
이 컬럼의 역할은 `부모` 엔티티에서 예를 들어 `영화` 타입의 엔티티를 조회할 수 있도록 한다.    
`@DiscriminatorColumn`이 해당역할을 하는 어노테이션이다.  
`name`을 별도로 지정하지 않을 시 `DTYPE`이 기본설정으로 생성된다.  
`자식`의 역할을하는 테이블의 코드는 아래와 같다.

~~~java
@Entity
@DiscriminatorValue("A")
public class MyAlbumV1 extends MyItemV1 {
    private String artist;
}
~~~