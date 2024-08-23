package org.sid.cvthequespringboot.record;

import org.sid.cvthequespringboot.dtos.Criteria;

import java.util.List;

public record CriteriaEvaluation(Long fileId, List<Criteria> acceptCriteria, List<Criteria> rejectCriteria) {
}