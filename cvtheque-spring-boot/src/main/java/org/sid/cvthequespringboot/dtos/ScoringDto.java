package org.sid.cvthequespringboot.dtos;

import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDto {
    private String profile;
    private Long score;
    private Date createdAt;

    @Column(length = 4000)
    private String message;
}
