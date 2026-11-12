package umc.fitty.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.fitty.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RunRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Integer durationMin;

    @Column(length = 255)
    private String diary;

    @Column(length = 255)
    private String imageUrl;

    public void setUserAndGoal(User user, Goal goal){
        this.user = user;
        this.goal = goal;
    }

}
