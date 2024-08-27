package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.FileVectorStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileStoreRepository extends JpaRepository<FileVectorStore, UUID> {
    FileVectorStore findByFileDB_Id(Long fileDBId);
    FileVectorStore findByFileDBId(Long fileDBId);
    void deleteByFileDB_Id(Long fileDBId);

    List<FileVectorStore> findAllByFolderId(Long folderId);

    void deleteAllByFolderId(Long folderId);
}
