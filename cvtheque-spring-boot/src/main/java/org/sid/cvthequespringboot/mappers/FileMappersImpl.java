package org.sid.cvthequespringboot.mappers;

import org.sid.cvthequespringboot.dtos.FileDbDto;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.Folder;
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
        return fileDB;
    }

    @Override
    public FileDbDto fromFileDB(FileDB fileDB) {
        FileDbDto fileDbDto = new FileDbDto();
        fileDbDto.setId(fileDB.getId());
        fileDbDto.setName(fileDB.getName());
        fileDbDto.setType(fileDB.getType());
        fileDbDto.setCreatedAt(fileDB.getCreatedAt());
        fileDbDto.setFolderId(fileDB.getFolder().getId());
        fileDbDto.setFolderName(fileDB.getFolder().getName());
        return fileDbDto;
    }

    @Override
    public FolderDto fromFolder(Folder folder) {
        FolderDto folderDto = new FolderDto();
        folderDto.setId(folder.getId());
        folderDto.setName(folder.getName());
        folderDto.setDescription(folder.getDescription());
        folderDto.setCreatedAt(folder.getCreatedAt());
        return folderDto;
    }

    @Override
    public Folder fromFolderDto(FolderDto folderDto) {
        Folder folder = new Folder();
        folder.setId(folderDto.getId());
        folder.setName(folderDto.getName());
        folder.setDescription(folderDto.getDescription());
        folder.setCreatedAt(folderDto.getCreatedAt());
        return folder;
    }
}
