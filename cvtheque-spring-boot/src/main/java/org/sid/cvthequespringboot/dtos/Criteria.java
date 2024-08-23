package org.sid.cvthequespringboot.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Criteria {
    private String name;
    private String message;
}
