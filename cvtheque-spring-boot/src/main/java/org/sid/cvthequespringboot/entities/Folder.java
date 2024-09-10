package org.sid.cvthequespringboot.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Date createdAt = new Date();

    @ManyToMany(cascade = {CascadeType.REMOVE}) // Ajout de CascadeType.REMOVE
    @JoinTable(
            name = "folder_files",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<FileDB> files;

}

    /*@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "folder_files",
            joinColumns = @JoinColumn(name = "folder_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<FileDB> files;*/