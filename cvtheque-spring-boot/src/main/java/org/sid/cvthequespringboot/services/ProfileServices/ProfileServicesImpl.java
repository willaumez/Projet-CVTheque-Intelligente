package org.sid.cvthequespringboot.services.ProfileServices;

import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.CriteriaDTO;
import org.sid.cvthequespringboot.dtos.ProfileDto;
import org.sid.cvthequespringboot.entities.CriteriaDB;
import org.sid.cvthequespringboot.entities.Profile;
import org.sid.cvthequespringboot.exceptions.CriteriaAlreadyExistsException;
import org.sid.cvthequespringboot.exceptions.CriteriaDeletionException;
import org.sid.cvthequespringboot.exceptions.ProfileAlreadyExistsException;
import org.sid.cvthequespringboot.mappers.FileMappersImpl;
import org.sid.cvthequespringboot.repositories.CriteriaDBRepository;
import org.sid.cvthequespringboot.repositories.ProfileRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileServicesImpl implements ProfileServices {
    private final ProfileRepository profileRepository;
    private final CriteriaDBRepository criteriaDBRepository;
    private final FileMappersImpl fileMappers;

    public ProfileServicesImpl(ProfileRepository profileRepository, CriteriaDBRepository criteriaDBRepository, FileMappersImpl fileMappers) {
        this.profileRepository = profileRepository;
        this.criteriaDBRepository = criteriaDBRepository;
        this.fileMappers = fileMappers;
    }

    @Override
    public ProfileDto save(Profile profile) {
        if (profileRepository.existsByName(profile.getName())) {
            throw new ProfileAlreadyExistsException("A profile with the name '" + profile.getName() + "' already exists.");
        }

        profile.setCreatedAt(new Date());
        Profile savedProfile = profileRepository.save(profile);
        return fileMappers.fromProfile(savedProfile);
    }

    @Override
    public Profile getProfile(Long id) {
        return null;
    }

    @Override
    public Profile updateProfile(Profile profile) {
        return null;
    }

    @Override
    public boolean deleteProfile(Long id) {
        return false;
    }

    @Override
    public List<ProfileDto> getProfiles() {
        List<Profile> profiles = profileRepository.findAllByOrderByCreatedAtDesc();

        return profiles.stream()
                .map(fileMappers::fromProfile)
                .collect(Collectors.toList());
    }


    @Override
    public List<CriteriaDTO> getCriteriaByProfileId(Long profileId) {
        List<CriteriaDB> criteriaList = criteriaDBRepository.findAllByProfileIdOrderByCreatedAtDesc(profileId);
        return criteriaList.stream()
                .map(fileMappers::fromCriteria)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public CriteriaDTO addCriteriaToProfile(Long profileId, String description) throws CriteriaAlreadyExistsException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        if (criteriaDBRepository.existsByProfileAndDescription(profile, description)) {
            throw new CriteriaAlreadyExistsException("This criteria already exists for this profile.");
        }
        CriteriaDB criteria = new CriteriaDB();
        criteria.setCreatedAt(new Date());
        criteria.setDescription(description);
        criteria.setProfile(profile);

        CriteriaDB newCriteria = criteriaDBRepository.save(criteria);
        return fileMappers.fromCriteria(newCriteria);
    }

    @Override
    public boolean deleteCriteria(Long id) throws CriteriaDeletionException {
        Optional<CriteriaDB> criteriaOpt = criteriaDBRepository.findById(id);
        if (criteriaOpt.isPresent()) {
            try {
                criteriaDBRepository.deleteById(id);
                return true;
            } catch (DataIntegrityViolationException e) {
                throw new CriteriaDeletionException("Cannot delete criteria due to related data.");
            } catch (Exception e) {
                throw new CriteriaDeletionException("Error occurred during deletion.");
            }
        } else {
            return false;
        }
    }




}
