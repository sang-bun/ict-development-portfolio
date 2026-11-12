package umc.fitty.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.fitty.config.security.SecurityUtil;
import umc.fitty.service.GoalService;
import umc.fitty.web.dto.GoalDTO.GoalCreateRequestDTO;
import umc.fitty.web.dto.GoalDTO.GoalDetailResponseDTO;
import umc.fitty.web.dto.GoalDTO.GoalResponseDTO;

@Tag(name = "Goal", description = "목표 관리 API")
@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @Operation(summary="목표 생성 (주간 1개 제한)")
    @PostMapping
    public ResponseEntity<GoalResponseDTO> create(@Valid @RequestBody GoalCreateRequestDTO req){
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(goalService.create(userId, req));
    }

    @Operation(summary = "이번 주 목표 조회", description = "이번 주(월~일) 목표와 요일별 달성 상태(DONE/MISS/FUTURE)를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<GoalDetailResponseDTO> getCurrentWeek() {
        return ResponseEntity.ok(goalService.getCurrentWeek());
    }
}
