package org.sid.cvthequespringboot.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileVectorStore {
//    UUID.randomUUID().toString()
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "METADATA", nullable = false, columnDefinition = "JSON")
    private String metadata;

/*    @Column(name = "EMBEDDING", columnDefinition = "VECTOR")
    private Object embedding; // Assuming a custom type is needed for VECTOR*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDB fileDB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_ID")
    private Folder folder;

}
