package org.sid.cvthequespringboot.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profiles", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private Date createdAt;

    @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private List<CriteriaProfile> listCriteria;
}