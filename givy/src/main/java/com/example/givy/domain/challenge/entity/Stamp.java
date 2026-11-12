package com.example.givy.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="stamp")
public class Stamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="stamp_id")
    private Long stampId;

    @Column(name="name", nullable = false, unique = true)
    private String name;

    @Column(name="image_url", nullable = false)
    private String imageUrl;
}