package com.example.givy.domain.challenge.entity;

import com.example.givy.domain.challenge.enums.Status;
import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="start_challenge")
public class StartChallenge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="start_challenge_id")
    private Long startChallengeId;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    //mapping
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name="stamp_id")
    private Stamp stamp;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    public void updateStatus(){
        this.status = Status.COMPLETED;
    }

    public void updateStamp(Stamp stamp){
        this.stamp = stamp;
    }
}
