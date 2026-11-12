package com.capstone.fertility.domain.mission.service.command;

import com.capstone.fertility.domain.mission.exception.MissionException;
import com.capstone.fertility.domain.mission.exception.code.MissionErrorCode;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SproutCycleCommandServiceImpl implements SproutCycleCommandService {

    private final UserRepository userRepository;

    @Override
    public void resetAfterRetest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));
        if (user.getCurrentLevel() != 5) {
            throw new MissionException(MissionErrorCode.SPROUT_RESET_REQUIRES_LEVEL_5);
        }
        user.resetProgressForNewCycle();
    }
}
