package umc.fitty.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.fitty.domain.common.BaseEntity;
import umc.fitty.domain.enums.CharacterType;

@Entity
@Table(name = "characters")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Character extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    @Builder.Default
    private int level = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CharacterType type;

    public void setUser(User user) {
        this.user = user;
    }

    public void setNameAndType(String name, CharacterType type) {
        this.name = name;
        this.type = type;
    }

}
