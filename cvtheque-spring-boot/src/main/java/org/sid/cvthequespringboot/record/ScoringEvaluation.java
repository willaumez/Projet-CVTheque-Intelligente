package org.sid.cvthequespringboot.record;

import org.sid.cvthequespringboot.dtos.Scoring;

import java.util.List;

public record ScoringEvaluation(Long fileId, Scoring scoring) {
}
