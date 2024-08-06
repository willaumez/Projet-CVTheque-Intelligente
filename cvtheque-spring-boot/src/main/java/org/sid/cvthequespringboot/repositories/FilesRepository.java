package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<FileDB, Long> {

}
