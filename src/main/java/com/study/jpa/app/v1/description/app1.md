#### App v1
주문 어플리케이션의 `version 1`이다.  
객체는 아래와 같다.

~~~java
@Entity
@Data
public class UserV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;
}

@Entity
public class OrderV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;
    @Column(name="USER_ID")
    private Long userId;
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public enum OrderStatus {
        ORDER, CANCEL
    }
}

@Entity
public class OrderItemV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;
    @Column(name = "ITEM_ID")
    private Long itemId;
    @Column(name = "ORDER_ID")
    private Long orderId;
    private int orderPrice;
    private int count;
}

@Entity
public class ItemV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}
~~~

위의 설계를 보고 문제가 보이지 않는다면,  
당신은 객체지향설계가 아닌 데이터베이스 기반 설계를 한 것이다.  

`Order`에서 `User` 데이터를 조회해보자.  

~~~java
@Test
void insertUser() {
    template((manager) -> {
        // insert user
        UserV1 userV1 = new UserV1();
        userV1.setName("name1");
        userV1.setCity("Seoul");
        userV1.setStreet("street1");
        userV1.setZipcode("00142");
        manager.persist(userV1);

        // insert order
        OrderV1 orderV1 = new OrderV1();
        orderV1.setUserId(userV1.getId());
        orderV1.setOrderDate(LocalDateTime.now());
        orderV1.setStatus(OrderV1.OrderStatus.ORDER);
        manager.persist(orderV1);

        // select user
        UserV1 findUser = manager.find(UserV1.class, orderV1.getUserId()); // 데이터베이스 지향 설계
        // UserV1 findUser = orderV1.getUserV1(); // 객체지향 설계
        log.info("findUser: {}", findUser);
    });
}
~~~

이제 문제가 보이는가?  
이제 진짜 JPA를 사용해보자.