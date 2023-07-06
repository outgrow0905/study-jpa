#### ManyToOne
다대일 관계에 대해서 알아보자.  
`플레이어`와 `팀`의 관계는 `팀`의 입장에서는 하나의 `팀`에 여러명의 `플레이어`가 매핑될 수 있다.  
반대로 `플레이어` 입장에서는 여러 `플레이어`가 하나의 `팀`에 매핑될 수 있다.  
따라서, `팀`의 입장에서는 `OneToMany`의 관계이고 `플레이어` 입장에서는 `ManyToOne`의 관계이다.  

이번 예시에서는 `플레이어` 입장에서 다대일 매핑을 알아보자.  



##### 다대일 단방향
`플레이어` 입장에서 단방향이므로,   
`플레이어`에서만 `팀`을 조회가 가능하고 `팀`에서는 `매핑된` 플레이어 리스트 탐색이 안되는 구성이다.  
객체 입장에서 `플레이어`에만 `팀`의 메모리주소가 있을 것이다.  
아래와 같이 코드를 짜면 될 것이다.  

~~~java
@Setter
@Getter
@ToString
@Entity
public class PlayerV1 {
    @Id
    @Column(name = "PLAYER_ID")
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private TeamV1 team;
}

@Setter
@Getter
@Entity
public class TeamV1 {
    @Id
    @Column(name = "TEAM_ID")
    private String id;

    private String name;

//    @OneToMany(mappedBy = "team")
//    private List<PlayerV1> players = new ArrayList<>();
}
~~~



##### 다대일 양방향
위의 구성에서 `팀`에서도 매핑된 `플레이어` 리스트를 조회하고 싶다면 다대일 양방향 구성이 된다.    
`Team` 클래스에 주석된 부분을 풀면 다대일 양방향 구성이 된다.  
다만 `mappedBy`를 통해 연관관계의 주인은 `Player`인 것을 알 수있다.  
따라서 `Team`에서 `Player`를 변경해도 실제 데이터베이스에서는 아무런 일도 일어나지 않는다.

~~~java
@Test
void notOwner() {
    template(manager -> {
        // team (transient)
        TeamV1 team1 = new TeamV1();
        team1.setId("team1");
        team1.setName("team Seoul");

        // player (transient)
        PlayerV1 player1 = new PlayerV1();
        player1.setId("player1");
        player1.setTeam(team1);
        player1.setName("name1");

        // add player to team
        team1.setPlayers(List.of(player1));

        // persist
        manager.persist(team1);
    });
}
~~~

위의 테스트코드에서는 `Team` 데이터만 `insert` 될 뿐, `Player` 테이블에는 어떠한 데이터도 들어가지 않는다.  



#### 주의할 점
연관관계의 주인은 `Player`이다.  
따라서, `Player`에 `Team`을 세팅하여 데이터를 영속화하면 `Player, Team` 모두 데이터베이스에 저장이 잘 될 것이다.
~~~java
@Test
void findFail() {
    template(manager -> {
        TeamV1 team = new TeamV1();
        team.setId("team1");
        team.setName("team Seoul");
        manager.persist(team);

        PlayerV1 player1 = new PlayerV1();
        player1.setId("player1");
        player1.setTeam(team);
        player1.setName("name1");
        manager.persist(player1);

        PlayerV1 player2 = new PlayerV1();
        player2.setId("player2");
        player2.setTeam(team);
        player2.setName("name2");
        manager.persist(player2);

        // fail
        // result: players: []
        log.info("players: {}", team.getPlayers());
    });
}
~~~

하지만 아래의 예시에서 `team.getPlayer()`에서는 빈 리스트를 반환환다.  
JPA에서 이러한 작업까지 자동으로 해주지는 않는다.  
따라서 `Team`에 `Player`를 매핑할떄는 `Player`에 `Team` 데이터를 넣어주어야 하고,  
`Player`에 `Team`을 매핑할떄는 `Team`에 `Player` 데이터를 넣어주어야 한다.  

양쪽에 다 코드작업을 해주게 되면 서로 호출하게되어 무한루프를 돌 위험이 있다.  
`Player` 클래스에 `편의용 메서드`를 만들어주자.

~~~java
@Entity
public class PlayerV1 {
    ...
    public void setTeam(TeamV1 team) {
        this.team = team;
        if (team != null // team을 null로 업데이트하면서 매핑을 제거하는 경우가 있을 수 있다.
                && !team.getPlayers().contains(this)) {
            team.getPlayers().add(this);
        }
    }
}
~~~

위의 코드도 버그가 있다. `null`로 업데이트하는 경우에 기존 `team`에서 `player`를 제거해주지 않기 때문이다.  
그냥 넘어가자.  
여기서 기억해야 할 것은 양방향 매핑을 해야하는 경우 `편의메서드`를 제공해야하는데 굉장히 번거롭다는 것이다.