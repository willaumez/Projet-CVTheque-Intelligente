package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.CriteriaDB;
import org.sid.cvthequespringboot.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriteriaDBRepository extends JpaRepository<CriteriaDB, Long> {
    List<CriteriaDB> findAllByOrderByCreatedAtDesc();

    List<CriteriaDB> findAllByProfileIdOrderByCreatedAtDesc(Long profileId);

    boolean existsByProfileAndDescription(Profile profile, String description);
}
