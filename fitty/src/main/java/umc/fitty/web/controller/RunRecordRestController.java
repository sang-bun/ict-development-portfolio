package umc.fitty.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.fitty.apiPayload.ApiResponse;
import umc.fitty.converter.RecordConverter;
import umc.fitty.domain.RunRecord;
import umc.fitty.service.RunRecordService.RecordCommandService;
import umc.fitty.service.RunRecordService.RecordQueryService;
import umc.fitty.web.dto.RunRecordDTO.RunRecordRequestDTO;
import umc.fitty.web.dto.RunRecordDTO.RunRecordResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class RunRecordRestController {

    private final RecordCommandService recordCommandService;
    private final RecordQueryService recordQueryService;

    @PostMapping
    public ApiResponse<RunRecordResponseDTO.RecordResultDTO> createRecord(@RequestBody RunRecordRequestDTO.CreateRecordDTO request) {
        RunRecord newRecord = recordCommandService.createRecord(request);

        return ApiResponse.success("기록이 성공적으로 생성되었습니다.", RecordConverter.toCreateResultDTO(newRecord));
    }

    @DeleteMapping("/{recordId}")
    public ApiResponse<String> deleteRecord(@PathVariable Long recordId) {
        recordCommandService.deleteRecord(recordId);
        return ApiResponse.success("기록이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/{recordId}")
    public ApiResponse<RunRecordResponseDTO.RecordDetailDTO> getRecord(@PathVariable Long recordId) {
        RunRecord record = recordQueryService.findRecord(recordId);
        return ApiResponse.success("기록 상세 조회 성공", RecordConverter.toDetailDTO(record));
    }
}
