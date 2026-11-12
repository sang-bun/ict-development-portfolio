package umc.fitty.service.RunRecordService;

import umc.fitty.domain.RunRecord;
import umc.fitty.web.dto.RunRecordDTO.RunRecordRequestDTO;

public interface RecordCommandService {

    RunRecord createRecord(RunRecordRequestDTO.CreateRecordDTO request);
    void deleteRecord(Long recordId);
}
