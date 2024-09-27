package org.sid.cvthequespringboot.services.FileServices;

import org.sid.cvthequespringboot.dtos.*;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.Folder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileServices {
    void processAndStoreFiles(MultipartFile file, FolderDto folderDto) throws IOException ;
    FileDB storeFiles(MultipartFile file, Folder folder) throws IOException;
    List<FileDto> getFiles();
    List<FileDto> getFilesByFolderId(Long folderId);
    File saveReadFile(Long fileId) throws IOException;
    void deleteFiles(List<Long> filesIds);

    void transferFiles(List<Long> fileIds, Long folderId);

    FileDB updateFile(FileDB fileDB);

    //Stats
    CVStatsDTO getCvStats();

    EvaluationDto getEvaluationByFileId(Long fileId);

    boolean deleteEvaluation(Long evaluationId);

    boolean deleteEvaluationByFileId(List<Long> selection);
}
