package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<FileDB, Long> {

    Long countByFolderId(Long id);

    List<FileDB> findAllByFolderIdOrderByCreatedAtDesc(Long id);

    void deleteAllByFolderId(Long id);

    List<FileDB> findAllByOrderByCreatedAtDesc();

    List<FileDB> findAllByFolderId(Long fromId);
}
