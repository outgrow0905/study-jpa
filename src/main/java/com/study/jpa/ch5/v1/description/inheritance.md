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


#### 단일테이블 전략 (SINGLE TABLE)
`단일테이블` 전략은 하나의 테이블로 구성하는 것이다.  
예를 들어 `영화, 책, 앨범`에 해당하는 컬럼들도 전부 `아이템` 테이블에 구성하는 것이다.  
당연히 `null 허용`으로 말이다.  

실무에서는 차라리 이런 형식으로 더 많이 쓰이는 것 같다.  
예를 들어 `정률, 정액할인`이 있다고 가정했을 떄에 사용해야하는 컬럼이 다르다고 가정해보자.  
`정률`은 `퍼센트`가 들어가야 하고, `정액`은 `금액`이 들어가야 할 것이다.  
이러한 경우에 `조인 전략`보다는 `단일 테이블`에 `퍼센트, 금액`을 모두 `null 허용`으로 하고 `정액, 정률`을 구분할 수 있는 `분류컬럼`을 추가하는 것이 더 많이 선호한다.  

예시는 위의 것을 그대로 사용하자.  

~~~java
@Entity
@DiscriminatorColumn(name = "DTYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class MyItemV2 {
    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
}
~~~

`@Inheritance`의 `strategy`만 바뀌고 나머지는 전부 그대로이다.  
상속받는 엔티티도 전부 그대로이다.  
단지 테이블 설계만 다르게 할 뿐이다.