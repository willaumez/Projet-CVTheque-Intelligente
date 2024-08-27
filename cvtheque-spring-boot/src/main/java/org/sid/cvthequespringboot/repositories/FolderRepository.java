package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.Folder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String kw);
}
