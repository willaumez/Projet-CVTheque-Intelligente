package org.sid.cvthequespringboot.services.ProfileServices;

import org.sid.cvthequespringboot.dtos.CriteriaProfileDto;
import org.sid.cvthequespringboot.dtos.ProfileDto;
import org.sid.cvthequespringboot.entities.Profile;
import org.sid.cvthequespringboot.exceptions.CriteriaAlreadyExistsException;
import org.sid.cvthequespringboot.exceptions.CriteriaDeletionException;

import java.util.List;

public interface ProfileServices {
    ProfileDto save(Profile profile);
    Profile getProfile(Long id);
    ProfileDto updateProfile(Profile profile);
    boolean deleteProfile(Long id);
    List<ProfileDto> getProfiles();

    //Criteria
    List<CriteriaProfileDto> getCriteriaByProfileId(Long profileId);
    CriteriaProfileDto addCriteriaToProfile(Long profileId, String description) throws CriteriaAlreadyExistsException;
    boolean deleteCriteria(Long id) throws CriteriaDeletionException;
}
