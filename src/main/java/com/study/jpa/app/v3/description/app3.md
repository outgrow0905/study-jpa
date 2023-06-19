#### App v3
주문 어플리케이션의 `version 3`이다.  
[version 2](../../v2/description/app2.md)에서 아래와 같은 새로운 요구사항이 들어왔다.  

~~~
- 상품을 주문할 때 배송정보를 입력할 수 있다. 주문과 배송은 일대일 관계이다.
- 상품을 카테고리로 구분할 수 있다.
~~~

요구사항을 하나씩 구현해보자.  



#### 배송정보
먼저, `주문`과 `배송`의 `외래키`를 어떤 테이블에서 관리할 지 정해야 한다.  
데이터베이스 관점에서는 어떤 테이블에 넣던지 상관없다.  
객체탐색 관점에서는 아무래도 `주문`에서 `배송`을 탐색하는 경우가 더 많을 것 같다.  

데이터베이스 `주문` 테이블에 `배송`의 `외래키`를 넣고,  
엔티티에서도 `외래키`의 주인을 `주문`으로 하여 설계해보자.

~~~java
@Entity
public class DeliveryV3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_ID")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    OrderV3 order;

    private String city;
    private String street;
    private String zipcode;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public enum DeliveryStatus {
        READY, COMPLETE
    }
}

@Entity
public class OrderV3 {
    ...
    @OneToOne
    @JoinColumn(name = "DELIVERY_ID")
    DeliveryV3 delivery;

    // 연관관계 관리 메서드
    public void setDelivery(DeliveryV3 delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
~~~


#### 카테고리
하나의 `상품`은 여러 `카테고리`에 속할 수 있다.  
반대로 하나의 `카테고리`에는 여러 `상품`이 속할 수 있다.  
`다대다` 관계이다.  

데이터베이스에는 매핑테이블이 당연히 존재하지만,  
엔티티에서는 없이 구현해보자.  
`상품`과 `카테고리` 두 테이블의 `외래키`만으로 매핑테이블이 있다고 가정했을 때에는  
매핑 엔티티 구현없이 코드만으로 간결하게 구현이 가능하다.  
실무에서는 이렇게 구현할 일이 거의 없곘지만 연습용코드로 하나 만들어두자.   

`상품`과 `카테고리`의 연관관계의 주인은 `카테고리`로 정하자.

~~~java
@Entity
public class CategoryV3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "CategoryV3_ItemV3",
            joinColumns = @JoinColumn(name = "CATEGORY_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID"))
    List<ItemV3> items;
}

@Entity
public class ItemV3 {
    ...
    
    @ManyToMany(mappedBy = "items")
    private List<CategoryV3> categories;
}
~~~