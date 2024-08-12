package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
}
