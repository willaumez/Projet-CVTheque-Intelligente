package org.sid.cvthequespringboot.mappers;


import org.sid.cvthequespringboot.dtos.FileDbDto;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.Folder;

public interface FileMappers {

    FileDB fromFileDbDto(FileDbDto fileDbDto);
    FileDbDto fromFileDB(FileDB fileDB);

    FolderDto fromFolder(Folder folder);
    Folder fromFolderDto(FolderDto folderDto);
}
