package com.example.givy.domain.user.entity;

import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user_securities_account")
public class UserSecuritiesAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="account_id")
    private Long accountId;

    @Column(name="securities_name", nullable = false)
    private String securitiesName;

    @Column(name="is_securities_connected", nullable = false)
    @Builder.Default
    private Boolean isSecuritiesConnected = false;

    @Column(name="connected_at")
    private LocalDateTime connectedAt;

    //mapping
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users users;
}