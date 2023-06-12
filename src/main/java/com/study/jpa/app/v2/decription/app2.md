#### App v2
주문 어플리케이션의 `version 2`이다.  
[version 1](../../v1/description/app1.md)을 개선해보자.  



#### User
하나의 `User`는 `Order`를 여러개 가질 수 있다.  
따라서 `User`와 `Order`의 관계는 `OneToMany`이다.  
반대로, `Order`와 `User`의 관계는 `ManyToOne`이 된다.  
JPA로 표현해보자.
~~~java
@Getter
@Setter
@Data
@Entity
public class UserV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;

    @OneToMany(mappedBy = "user")
    List<OrderV2> orders = new ArrayList<>();
}
~~~
`mappedBy`는 `User테이블에 외래키가 없다`정도로 이해하고 넘어가자.  



#### Order
`Order`는 위의 `User`와 `ManyToOne` 관계이다.
`Order`와 `OrderItem`은 `OneToMany` 관계이다.  
위와 마찬가지로 `Order` 테이블에는 `OrderItem`의 외래키가 없으므로 `maapedBy`를 해야한다 정도로 넘어가자.  

`setUser, addOrderItem` 메서드는 객체를 양방으로 관리하기위한 번거로운 추가메서드로 생각하자.  
~~~java
@Getter
@Setter
@Entity
public class OrderV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @JoinColumn(name="USER_ID")
    @ManyToOne
    private UserV2 user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItemV2> orderItems = new ArrayList<>();

    public void setUser(UserV2 user) {
        if (this.user != null) {
            this.user.getOrders().remove(this);
        }

        this.user = user;
        this.user.getOrders().add(this);
    }

    public void addOrderItem(OrderItemV2 orderItem) {
        orderItem.setOrder(this);
        orderItems.add(orderItem);
    }

    public enum OrderStatus {
        ORDER, CANCEL
    }
}
~~~



#### OrderItem
`OrderItem`은 `Order`와 `Item`을 연결해주는 테이블이다.  
따라서, `Order, Item`과는 모두 `ManyToOne`의 관계이다.  
~~~java
@Getter
@Setter
@Entity
@NoArgsConstructor
public class OrderItemV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @JoinColumn(name = "ITEM_ID")
    @ManyToOne
    private ItemV2 item;

    @JoinColumn(name = "ORDER_ID")
    @ManyToOne
    private OrderV2 order;

    private int orderPrice;

    private int count;

    public OrderItemV2(ItemV2 item, int count) {
        this.item = item;
        this.count = count;
        this.orderPrice = item.getPrice() * count;
    }
}
~~~



#### Item
`Item`은 수정사항이 없다.  
`Item`을 기준으로 `OrderItem`을 탐색할 필요가 없기 때문이다.  
참고로 `Item`과 `OrderItem`은 `OneToMany`의 관계이다.  
~~~java
@Getter
@Setter
@Entity
public class ItemV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}
~~~



#### 테스트
테스트코드는 아래와 같다.  
굳이 번거롭게 양방향 객체관리를 해야하는 이유는  
첫번째 `template`에서 자동으로 `Entity`들에 데이터를 넣어주지 않기 때문이다.  
~~~java
@Test
void insertUser() {
    template((manager) -> {
        // insert user
        UserV2 user1 = new UserV2();
        user1.setName("name1");
        user1.setCity("Seoul");
        user1.setStreet("street1");
        user1.setZipcode("00142");
        manager.persist(user1);

        // insert order
        OrderV2 order1 = new OrderV2();
        order1.setUser(user1);
        order1.setOrderDate(LocalDateTime.now());
        order1.setStatus(OrderV2.OrderStatus.ORDER);
        manager.persist(order1);

        // insert item
        ItemV2 item1 = new ItemV2();
        item1.setName("item name1");
        item1.setPrice(100);
        item1.setStockQuantity(10);
        manager.persist(item1);

        ItemV2 item2 = new ItemV2();
        item2.setName("item name2");
        item2.setPrice(200);
        item2.setStockQuantity(20);
        manager.persist(item2);

        // insert order-item
        OrderItemV2 orderItem1 = new OrderItemV2(item1, 2);
        order1.addOrderItem(orderItem1);
        manager.persist(orderItem1);

        OrderItemV2 orderItem2 = new OrderItemV2(item2, 2);
        order1.addOrderItem(orderItem2);
        manager.persist(orderItem2);
    });

    template(manager -> {
        UserV2 user1 = manager.find(UserV2.class, 1L);

        // find order by member
        OrderV2 order1 = user1.getOrders().get(0);
        log.info("findOrder: {}", order1);

        // find member by order
        UserV2 findUser = order1.getUser();
        log.info("findUser: {}", findUser);

        // find order-item by order
        List<OrderItemV2> findOrderItems = order1.getOrderItems();
        log.info("findOrderItems: {}", findOrderItems);

        // find item by order
        ItemV2 findItem = findOrderItems.get(0).getItem();
        log.info("findItem: {}", findItem);
    });
}
~~~
