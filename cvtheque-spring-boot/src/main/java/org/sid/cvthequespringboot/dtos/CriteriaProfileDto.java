package org.sid.cvthequespringboot.dtos;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaProfileDto {
    private Long id;
    private String description;
    private Date createdAt;
}
