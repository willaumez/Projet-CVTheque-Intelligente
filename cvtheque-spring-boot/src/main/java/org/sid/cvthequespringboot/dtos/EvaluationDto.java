package org.sid.cvthequespringboot.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.sid.cvthequespringboot.entities.CriteriaEval;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class EvaluationDto {
    private Long id;

    private Date createdAt = new Date();

    private List<CriteriaEval> acceptCriteria;

    private List<CriteriaEval> rejectCriteria;

    private List<String> existWords;

    private List<String> noExistWords;

    private List<ScoringDto> scoring;
}
