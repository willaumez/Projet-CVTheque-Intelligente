package org.sid.cvthequespringboot.services.FileServices;

import org.sid.cvthequespringboot.dtos.CVStatsDTO;
import org.sid.cvthequespringboot.dtos.FileDbDto;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.Folder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface FileServices {
    void processAndStoreFiles(MultipartFile file, FolderDto folderDto) throws IOException ;
    FileDB storeFiles(MultipartFile file, Folder folder) throws IOException;
    List<FileDbDto> getFiles();
    List<FileDbDto> getFilesByFolderId(Long folderId);
    File saveReadFile(Long fileId) throws IOException;
    void deleteFiles(List<Long> filesIds);

    void transferFiles(List<Long> fileIds, Long folderId);

    FileDB updateFile(FileDB fileDB);

    //Stats
    List<CVStatsDTO> getCvStats();

}
