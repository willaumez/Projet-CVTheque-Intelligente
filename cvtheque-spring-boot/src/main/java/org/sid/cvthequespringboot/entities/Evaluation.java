package org.sid.cvthequespringboot.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt = new Date();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "evaluation_id")
    private List<CriteriaEval> acceptRejectCriteria;

    @ElementCollection
    @CollectionTable(name = "evaluation_exist_words", joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "exist_words")
    private List<String> existWords;

    @ElementCollection
    @CollectionTable(name = "evaluation_no_exist_words", joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "no_exist_words")
    private List<String> noExistWords;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "evaluation_id")
    private List<Scoring> scoring;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileDB fileDB;
}
