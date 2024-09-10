package org.sid.cvthequespringboot.mappers;


import org.sid.cvthequespringboot.dtos.CriteriaDTO;
import org.sid.cvthequespringboot.dtos.FileDbDto;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.dtos.ProfileDto;
import org.sid.cvthequespringboot.entities.CriteriaDB;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.entities.Profile;

public interface FileMappers {

    FileDB fromFileDbDto(FileDbDto fileDbDto);
    FileDbDto fromFileDB(FileDB fileDB);

    FolderDto fromFolder(Folder folder);
    Folder fromFolderDto(FolderDto folderDto);

    ProfileDto fromProfile(Profile profile);
    Profile fromProfileDto(ProfileDto profileDto);

    CriteriaDTO fromCriteria(CriteriaDB criteriaDB);
    CriteriaDB fromCriteriaDTO(CriteriaDTO criteriaDTO);
}
