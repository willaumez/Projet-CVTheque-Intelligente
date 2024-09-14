package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.CriteriaProfile;
import org.sid.cvthequespringboot.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriteriaProfileRepository extends JpaRepository<CriteriaProfile, Long> {
    List<CriteriaProfile> findAllByOrderByCreatedAtDesc();
    List<CriteriaProfile> findAllByProfileIdOrderByCreatedAtDesc(Long profileId);
    boolean existsByProfileAndDescription(Profile profile, String description);
    void deleteAllByProfileId(Long id);

    void deleteAllByProfile(Profile profile);
}
