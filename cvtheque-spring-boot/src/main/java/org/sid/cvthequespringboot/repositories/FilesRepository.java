package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.dtos.GraphData;
import org.sid.cvthequespringboot.dtos.HeaderData;
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
    @Query("SELECT new org.sid.cvthequespringboot.dtos.GraphData(f.folder.name, EXTRACT(MONTH FROM f.createdAt), EXTRACT(YEAR FROM f.createdAt), COUNT(f)) " +
            "FROM FileDB f " +
            "GROUP BY f.folder.name, EXTRACT(YEAR FROM f.createdAt), EXTRACT(MONTH FROM f.createdAt) " +
            "ORDER BY f.folder.name, EXTRACT(YEAR FROM f.createdAt), EXTRACT(MONTH FROM f.createdAt)")
    List<GraphData> countCVsByMonthAndFolder();


    //Stats
    @Query("SELECT COUNT(f) FROM FileDB f")
    long getTotalFiles();

    @Query("SELECT COUNT(f) FROM Folder f")
    long getTotalFolders();

    @Query("SELECT COUNT(p) FROM Profile p")
    long getTotalProfiles();

    @Query("SELECT COUNT(f) FROM FileDB f WHERE f.evaluation IS NOT NULL")
    long getTotalEvaluatedFiles();

    @Query("SELECT COUNT(f) FROM FileDB f WHERE f.evaluation IS NULL")
    long getTotalNotEvaluatedFiles();

    @Query("SELECT " +
            "COUNT(f) AS totalFiles, " +
            "COUNT(fol) AS totalFolders, " +
            "COUNT(p) AS totalProfiles, " +
            "SUM(CASE WHEN f.evaluation IS NOT NULL THEN 1 ELSE 0 END) AS totalEvaluated, " +
            "SUM(CASE WHEN f.evaluation IS NULL THEN 1 ELSE 0 END) AS totalNotEvaluated " +
            "FROM FileDB f, Folder fol, Profile p")
    HeaderData getCVStats();

}
