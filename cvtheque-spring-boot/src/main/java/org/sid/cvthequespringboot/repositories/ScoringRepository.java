package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.Scoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoringRepository extends JpaRepository<Scoring, Long> {
    List<Scoring> findAllByOrderByCreatedAtDesc();

}
