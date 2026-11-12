package umc.fitty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.domain.mapping.Notification;
import umc.fitty.domain.User;
import umc.fitty.repository.NotificationRepository;
import umc.fitty.repository.UserRepository;
import umc.fitty.web.dto.NotificationDTO.NotificationPatchRequestDTO;
import umc.fitty.web.dto.NotificationDTO.NotificationResponseDTO;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public NotificationResponseDTO getMySettings(Long userId) {
        User user = getActiveUser(userId);
        Notification setting = notificationRepository.findByUser(user)
                .orElseGet(() -> defaultSetting(user));

        return toResponse(setting);
    }

    @Transactional
    public void patchMySettings(Long userId, NotificationPatchRequestDTO req) {
        User user = getActiveUser(userId);

        Notification setting = notificationRepository.findByUser(user)
                .orElseGet(() -> notificationRepository.save(defaultSetting(user)));

        setting.update(
                req.getPushEnabled(),
                req.getReminderEnabled(),
                parseTimeOrNull(req.getReminderTime())
        );
    }

    private User getActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (user.isDeleted()) {
            throw new IllegalStateException("탈퇴한 사용자입니다.");
        }
        return user;
    }

    private Notification defaultSetting(User user) {
        // 초기 기본값: 푸시 ON, 리마인더 OFF
        return Notification.builder()
                .user(user)
                .pushEnabled(true)
                .reminderEnabled(false)
                .reminderTime(null)
                .build();
    }

    private NotificationResponseDTO toResponse(Notification n) {
        return NotificationResponseDTO.builder()
                .pushEnabled(n.isPushEnabled())
                .reminderEnabled(n.isReminderEnabled())
                .reminderTime(n.getReminderTime() == null ? null : n.getReminderTime().toString())
                .build();
    }

    private LocalTime parseTimeOrNull(String hhmm) {
        return (hhmm == null || hhmm.isBlank()) ? null : LocalTime.parse(hhmm);
    }
}
