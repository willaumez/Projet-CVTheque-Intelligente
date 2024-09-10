package org.sid.cvthequespringboot.web;

import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.dtos.CriteriaDTO;
import org.sid.cvthequespringboot.dtos.ProfileDto;
import org.sid.cvthequespringboot.entities.CriteriaDB;
import org.sid.cvthequespringboot.entities.Profile;
import org.sid.cvthequespringboot.exceptions.CriteriaAlreadyExistsException;
import org.sid.cvthequespringboot.exceptions.CriteriaDeletionException;
import org.sid.cvthequespringboot.exceptions.ProfileAlreadyExistsException;
import org.sid.cvthequespringboot.services.ProfileServices.ProfileServicesImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileRestController {
    private final ProfileServicesImpl profileService;

    @PostMapping("/save")
    public ResponseEntity<?> saveProfile(@RequestBody Profile profile) {
        try {
            ProfileDto savedProfile = profileService.save(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (ProfileAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/profiles")
    public ResponseEntity<?> getProfiles() {
        try {
            return ResponseEntity.ok(profileService.getProfiles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/criteria/{profileId}")
    public ResponseEntity<?> getCriteriaByProfileId(@PathVariable Long profileId) {
        try {
            List<CriteriaDTO> criteriaList = profileService.getCriteriaByProfileId(profileId);
            return ResponseEntity.ok(criteriaList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/criteria/add")
    public ResponseEntity<?> addCriteriaToProfile(@RequestBody CriteriaDTO criteriaDTO) {
        try {
            CriteriaDTO newCriteria = profileService.addCriteriaToProfile(criteriaDTO.getId(), criteriaDTO.getDescription());
            return ResponseEntity.ok(newCriteria);
        } catch (CriteriaAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    @DeleteMapping("/criteria/delete/{id}")
    public ResponseEntity<?> deleteCriteria(@PathVariable Long id) {
        try {
            boolean isDeleted = profileService.deleteCriteria(id);
            if (isDeleted) {
                return ResponseEntity.ok("Criteria deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Criteria not found");
            }
        } catch (CriteriaDeletionException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred."+e.getMessage());
        }
    }



}
