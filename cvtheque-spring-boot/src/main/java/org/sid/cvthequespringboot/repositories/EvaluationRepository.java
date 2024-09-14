package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findAllByOrderByCreatedAtDesc();

    Evaluation findByFileDB_Id(Long id);
}
