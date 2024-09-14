package org.sid.cvthequespringboot.entities;

import jakarta.persistence.*;
import lombok.*;
import org.sid.cvthequespringboot.enums.Status;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriteriaEval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private Status status;

    private String name;
    private String message;
}
