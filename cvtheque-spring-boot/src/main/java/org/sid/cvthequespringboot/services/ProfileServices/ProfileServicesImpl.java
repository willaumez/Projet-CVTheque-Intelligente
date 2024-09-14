package org.sid.cvthequespringboot.services.ProfileServices;

import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.CriteriaProfileDto;
import org.sid.cvthequespringboot.dtos.ProfileDto;
import org.sid.cvthequespringboot.entities.CriteriaProfile;
import org.sid.cvthequespringboot.entities.Profile;
import org.sid.cvthequespringboot.exceptions.CriteriaAlreadyExistsException;
import org.sid.cvthequespringboot.exceptions.CriteriaDeletionException;
import org.sid.cvthequespringboot.exceptions.ProfileAlreadyExistsException;
import org.sid.cvthequespringboot.mappers.FileMappersImpl;
import org.sid.cvthequespringboot.repositories.CriteriaProfileRepository;
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
    private final CriteriaProfileRepository criteriaProfileRepository;
    private final FileMappersImpl fileMappers;

    public ProfileServicesImpl(ProfileRepository profileRepository, CriteriaProfileRepository criteriaProfileRepository, FileMappersImpl fileMappers) {
        this.profileRepository = profileRepository;
        this.criteriaProfileRepository = criteriaProfileRepository;
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
    public ProfileDto updateProfile(Profile profile) {
        if (profile.getId() != null) {
            Optional<Profile> profileOpt = profileRepository.findById(profile.getId());

            if (profileOpt.isPresent()) {
                Profile existingProfile = profileOpt.get();
                existingProfile.setName(profile.getName());
                existingProfile.setDescription(profile.getDescription());
                existingProfile.setCreatedAt(new Date());
                Profile updatedProfile = profileRepository.save(existingProfile);
                return fileMappers.fromProfile(updatedProfile);
            }
        }
        return fileMappers.fromProfile(profile);
    }


    @Override
    @Transactional
    public boolean deleteProfile(Long id) {
        Optional<Profile> profileOpt = profileRepository.findById(id);
        if (profileOpt.isPresent()) {
            try {
                Profile profile = profileOpt.get();
                //profileRepository.delete(profile);
                System.out.println("==================== Profile deleted"+profile.getId());
                criteriaProfileRepository.deleteAllByProfileId(profile.getId());
                profileRepository.deleteById(id);
                return true;
            } catch (DataIntegrityViolationException e) {
                throw new CriteriaDeletionException("Cannot delete profile due to related data.");
            } catch (Exception e) {
                throw new CriteriaDeletionException("Error occurred during deletion.");
            }
        } else {
            return false;
        }
    }

    @Override
    public List<ProfileDto> getProfiles() {
        List<Profile> profiles = profileRepository.findAllByOrderByCreatedAtDesc();

        return profiles.stream()
                .map(fileMappers::fromProfile)
                .collect(Collectors.toList());
    }


    @Override
    public List<CriteriaProfileDto> getCriteriaByProfileId(Long profileId) {
        List<CriteriaProfile> criteriaList = criteriaProfileRepository.findAllByProfileIdOrderByCreatedAtDesc(profileId);
        return criteriaList.stream()
                .map(fileMappers::fromCriteria)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public CriteriaProfileDto addCriteriaToProfile(Long profileId, String description) throws CriteriaAlreadyExistsException {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        if (criteriaProfileRepository.existsByProfileAndDescription(profile, description)) {
            throw new CriteriaAlreadyExistsException("This criteria already exists for this profile.");
        }
        CriteriaProfile criteria = new CriteriaProfile();
        criteria.setCreatedAt(new Date());
        criteria.setDescription(description);
        criteria.setProfile(profile);

        CriteriaProfile newCriteria = criteriaProfileRepository.save(criteria);
        return fileMappers.fromCriteria(newCriteria);
    }

    @Override
    public boolean deleteCriteria(Long id) throws CriteriaDeletionException {
        Optional<CriteriaProfile> criteriaOpt = criteriaProfileRepository.findById(id);
        if (criteriaOpt.isPresent()) {
            try {
                criteriaProfileRepository.deleteById(id);
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
