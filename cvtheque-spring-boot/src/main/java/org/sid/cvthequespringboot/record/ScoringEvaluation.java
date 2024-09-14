package org.sid.cvthequespringboot.record;

import org.sid.cvthequespringboot.dtos.ScoringDto;

public record ScoringEvaluation(Long fileId, ScoringDto scoringDto) {
}
