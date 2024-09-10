package org.sid.cvthequespringboot.mappers;

import org.sid.cvthequespringboot.dtos.CriteriaDTO;
import org.sid.cvthequespringboot.dtos.FileDbDto;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.dtos.ProfileDto;
import org.sid.cvthequespringboot.entities.CriteriaDB;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.entities.Profile;
import org.springframework.stereotype.Service;

@Service
public class FileMappersImpl implements FileMappers {
    @Override
    public FileDB fromFileDbDto(FileDbDto fileDbDto) {
        FileDB fileDB = new FileDB();
        fileDB.setId(fileDbDto.getId());
        fileDB.setName(fileDbDto.getName());
        fileDB.setType(fileDbDto.getType());
        fileDB.setCreatedAt(fileDbDto.getCreatedAt());
        if (fileDbDto.getFolderId() != null || fileDbDto.getFolderName() != null) {
            Folder folder = new Folder();
            folder.setId(fileDbDto.getFolderId());
            folder.setName(fileDbDto.getFolderName());
            fileDB.setFolder(folder);
        } else {
            fileDB.setFolder(null);
        }
        return fileDB;
    }

    /*public FileDB fromFileDbDto(FileDbDto fileDbDto) {
        FileDB fileDB = new FileDB();
        fileDB.setId(fileDbDto.getId());
        fileDB.setName(fileDbDto.getName());
        fileDB.setType(fileDbDto.getType());
        fileDB.setCreatedAt(fileDbDto.getCreatedAt());
        return fileDB;
    }*/

    @Override
    public FileDbDto fromFileDB(FileDB fileDB) {
        FileDbDto fileDbDto = new FileDbDto();
        fileDbDto.setId(fileDB.getId());
        fileDbDto.setName(fileDB.getName());
        fileDbDto.setType(fileDB.getType());
        fileDbDto.setCreatedAt(fileDB.getCreatedAt());
        if (fileDB.getFolder() != null) {
            fileDbDto.setFolderId(fileDB.getFolder().getId());
            fileDbDto.setFolderName(fileDB.getFolder().getName());
        } else {
            fileDbDto.setFolderId(null);
            fileDbDto.setFolderName(null);
        }

        return fileDbDto;
    }

    /*public FileDbDto fromFileDB(FileDB fileDB) {
        FileDbDto fileDbDto = new FileDbDto();
        fileDbDto.setId(fileDB.getId());
        fileDbDto.setName(fileDB.getName());
        fileDbDto.setType(fileDB.getType());
        fileDbDto.setCreatedAt(fileDB.getCreatedAt());
        fileDbDto.setFolderId(fileDB.getFolder().getId());
        fileDbDto.setFolderName(fileDB.getFolder().getName());
        return fileDbDto;
    }*/

    @Override
    public FolderDto fromFolder(Folder folder) {
        if (folder == null) {
            return null;
        }
        FolderDto folderDto = new FolderDto();
        folderDto.setId(folder.getId());
        folderDto.setName(folder.getName());
        folderDto.setDescription(folder.getDescription());
        folderDto.setCreatedAt(folder.getCreatedAt());
        return folderDto;
    }

    @Override
    public Folder fromFolderDto(FolderDto folderDto) {
        if (folderDto  == null) {
            return null;
        }
        Folder folder = new Folder();
        folder.setId(folderDto.getId());
        folder.setName(folderDto.getName());
        folder.setDescription(folderDto.getDescription());
        folder.setCreatedAt(folderDto.getCreatedAt());
        return folder;
    }

    @Override
    public ProfileDto fromProfile(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(profile.getId());
        profileDto.setName(profile.getName());
        profileDto.setDescription(profile.getDescription());
        profileDto.setCreatedAt(profile.getCreatedAt());

        // Tu peux aussi ajouter une logique pour convertir la liste des critères si nécessaire
        return profileDto;
    }

    @Override
    public Profile fromProfileDto(ProfileDto profileDto) {
        if (profileDto == null) {
            return null;
        }

        Profile profile = new Profile();
        profile.setId(profileDto.getId());
        profile.setName(profileDto.getName());
        profile.setDescription(profileDto.getDescription());
        profile.setCreatedAt(profileDto.getCreatedAt());

        // Ajouter une logique pour gérer la liste des critères si nécessaire
        return profile;
    }

    @Override
    public CriteriaDTO fromCriteria(CriteriaDB criteriaDB) {
        if (criteriaDB == null) {
            return null;
        }

        CriteriaDTO criteriaDTO = new CriteriaDTO();
        criteriaDTO.setId(criteriaDB.getId());
        criteriaDTO.setDescription(criteriaDB.getDescription());
        criteriaDTO.setCreatedAt(criteriaDB.getCreatedAt());

        return criteriaDTO;
    }

    @Override
    public CriteriaDB fromCriteriaDTO(CriteriaDTO criteriaDTO) {
        if (criteriaDTO == null) {
            return null;
        }

        CriteriaDB criteriaDB = new CriteriaDB();
        criteriaDB.setDescription(criteriaDTO.getDescription());
        criteriaDB.setCreatedAt(criteriaDTO.getCreatedAt());

        // Associer le critère à un profil (cela nécessite que tu aies accès à l'entité Profile par son ID)
        Profile profile = new Profile();
        profile.setId(criteriaDTO.getId());
        criteriaDB.setProfile(profile);

        return criteriaDB;
    }


}
