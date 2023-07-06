#### OneToOne
일대일 관계를 알아보자.    
`회원` 한명이 하나의 `사물함`만 가질 수 있다면, 이는 일대일 관계일것이다.  

이러한 경우 외래키의 주인은 회원 혹은 사물함 아무나 될 수 있다.  
실제 테이블에서 `외래키`의 위치도 다대일에서는 다에만 위치할 수 있었지만,   
일대일 관계에서 `외래키`는 둘 중 아무곳에나 위치할 수 있다.  

실제 `외래키`가 `회원`에 있든, `사물함`에 있든   
비지니스 중요도에 따라 `주 테이블`은 `회원`이고 `대상 테이블`은 `사물함`이다.  
어느 곳에 `외래키`를 위치시켜야 할까?  

#### 일대일 관계에서 외래키의 위치
`회원` 테이블에 `외래키`가 있다고 가정해보자.  
`주 객체`로부터 `대상 객체`를 조회할 수 있으니 객체지향적으로 직관적이다.  
`사물함` 테이블에 `외래키`가 있다고 가정해보자.  
`대상 테이블`에 `외래키`가 있어서 `대상 객체`로부터 `주 객체`를 조회해야하니 객체지향 관점에서 그 반대보다 덜 매력적이다.  

하지만, 한 `회원`이 어러개의 `사물함`을 가지도록 변경되었다고 가정해보자.  
이 경우에 `대상 테이블`에 `외래키`가 있으면 테이블 변경이 필요없다.  
하지만, `주 테이블`에 `외래키`가 있었다면 매핑테이블을 만들던지 구조변경이 불가피할 것이다.  

이렇게 장단점이 있는 만큼 둘 다 알아보자.

#### 주 테이블에 외래키
`회원` 테이블에 `외래키`를 위치시키고 `회원`과 `사물함`의 객체코드를 만들어보자.  
일대일 관계이니 바로 양방향으로 만들어보자.

#### 일대일 양방향 
~~~java
@Entity
public class LockerMemberV1 {
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private LockerV1 locker;
}

@Entity
public class LockerV1 {
    @Id
    @Column(name = "LOCKER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String location;

    @OneToOne(mappedBy = "locker")
    private LockerMemberV1 member;
}
~~~



#### 대상 테이블에 외래키
`사물함` 테이블에 `외래키`를 위치시키고 `회원`과 `사물함`의 객체코드를 만들어보자.  
바로 양방향으로 만들어보자.  

~~~java
@Entity
public class LockerMemberV2 {
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToOne(mappedBy = "member")
    private LockerV2 locker;
}

@Entity
public class LockerV2 {
    @Id
    @Column(name = "LOCKER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String location;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private LockerMemberV2 member;
}
~~~