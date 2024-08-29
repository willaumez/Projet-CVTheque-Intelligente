package org.sid.cvthequespringboot.record;

import org.sid.cvthequespringboot.dtos.Criteria;

import java.util.List;

public record KeywordsEvaluation(Long fileId, List<String> existWords, List<String> noExistWords) {
}
