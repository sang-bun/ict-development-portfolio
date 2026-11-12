package umc.fitty.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import umc.fitty.domain.User;

import java.time.LocalTime;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean pushEnabled;

    @Column(nullable = false)
    private boolean reminderEnabled;

    private LocalTime reminderTime;     // nullable: reminderEnabled=false 일 때 null 가능

    public void update(Boolean pushEnabled,
                       Boolean reminderEnabled,
                       LocalTime reminderTime
                       ) {

        if (pushEnabled != null) this.pushEnabled = pushEnabled;
        if (reminderEnabled != null) this.reminderEnabled = reminderEnabled;

        // reminderEnabled=false면 time을 비워두도록 정책
        if (reminderEnabled != null && !reminderEnabled) {
            this.reminderTime = null;
        } else if (reminderTime != null) {
            this.reminderTime = reminderTime;
        }
    }
}
