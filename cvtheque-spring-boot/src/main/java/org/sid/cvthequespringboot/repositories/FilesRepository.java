package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.dtos.CVStatsDTO;
import org.sid.cvthequespringboot.entities.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<FileDB, Long> {

    Long countByFolderId(Long id);

    List<FileDB> findAllByFolderIdOrderByCreatedAtDesc(Long id);

    void deleteAllByFolderId(Long id);

    List<FileDB> findAllByOrderByCreatedAtDesc();

    List<FileDB> findAllByFolderId(Long fromId);

    //Stats
    /*@Query("SELECT new org.sid.cvthequespringboot.dtos.CVStatsDTO(f.folder.name, extract(month from f.createdAt), count(f)) " +
            "FROM FileDB f " +
            "GROUP BY f.folder.name, extract(month from f.createdAt) " +
            "ORDER BY f.folder.name, extract(month from f.createdAt)")
    List<CVStatsDTO> countCVsByMonthAndFolder();*/

    @Query("SELECT new org.sid.cvthequespringboot.dtos.CVStatsDTO(f.folder.name, EXTRACT(MONTH FROM f.createdAt), EXTRACT(YEAR FROM f.createdAt), COUNT(f)) " +
            "FROM FileDB f " +
            "GROUP BY f.folder.name, EXTRACT(YEAR FROM f.createdAt), EXTRACT(MONTH FROM f.createdAt) " +
            "ORDER BY f.folder.name, EXTRACT(YEAR FROM f.createdAt), EXTRACT(MONTH FROM f.createdAt)")
    List<CVStatsDTO> countCVsByMonthAndFolder();


}
