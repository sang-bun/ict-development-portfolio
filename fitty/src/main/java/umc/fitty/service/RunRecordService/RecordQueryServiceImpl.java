package umc.fitty.service.RunRecordService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.apiPayload.code.ErrorCode;
import umc.fitty.apiPayload.exception.CustomException;
import umc.fitty.domain.RunRecord;
import umc.fitty.repository.RecordRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordQueryServiceImpl implements RecordQueryService {

    private final RecordRepository recordRepository;


    @Override
    public RunRecord findRecord(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));
    }
}
