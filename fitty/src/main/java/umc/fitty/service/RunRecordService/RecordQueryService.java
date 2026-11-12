package umc.fitty.service.RunRecordService;

import umc.fitty.domain.RunRecord;

public interface RecordQueryService {
    RunRecord findRecord(Long recordId);
}
