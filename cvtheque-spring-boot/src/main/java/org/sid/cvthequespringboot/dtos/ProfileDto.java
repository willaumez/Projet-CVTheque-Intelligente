package org.sid.cvthequespringboot.dtos;

import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
}
