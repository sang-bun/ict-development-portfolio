package umc.fitty.converter;

import umc.fitty.domain.RunRecord;
import umc.fitty.web.dto.RunRecordDTO.RunRecordRequestDTO;
import umc.fitty.web.dto.RunRecordDTO.RunRecordResponseDTO;

public class RecordConverter {

    public static RunRecord toRunRecord(RunRecordRequestDTO.CreateRecordDTO request){
        return RunRecord.builder()
                .distance(request.getDistance())
                .durationMin(request.getDurationMin())
                .diary(request.getDiary())
                .imageUrl(request.getImageUrl())
                .build();
    }

    public static RunRecordResponseDTO.RecordResultDTO toCreateResultDTO(RunRecord runRecord){
        return RunRecordResponseDTO.RecordResultDTO.builder()
                .recordId(runRecord.getId())
                .createdAt(runRecord.getCreatedAt())
                .build();
    }

    public static RunRecordResponseDTO.RecordDetailDTO toDetailDTO(RunRecord runRecord){
        return RunRecordResponseDTO.RecordDetailDTO.builder()
                .recordId(runRecord.getId())
                .distance(runRecord.getDistance())
                .durationMin(runRecord.getDurationMin())
                .diary(runRecord.getDiary())
                .imageUrl(runRecord.getImageUrl())
                .createdAt(runRecord.getCreatedAt())
                .build();

    }
}
