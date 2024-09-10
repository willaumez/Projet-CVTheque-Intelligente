package org.sid.cvthequespringboot.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Scoring {
    private Long score;
    private String message;
}
