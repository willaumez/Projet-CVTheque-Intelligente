package org.sid.cvthequespringboot.repositories;

import org.sid.cvthequespringboot.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByOrderByCreatedAtDesc();

    boolean existsByName(String name);
}
