package org.sid.cvthequespringboot.dtos;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDto {
    private String profile;
    private Long score;

    @Column(length = 4000)
    private String message;
}
