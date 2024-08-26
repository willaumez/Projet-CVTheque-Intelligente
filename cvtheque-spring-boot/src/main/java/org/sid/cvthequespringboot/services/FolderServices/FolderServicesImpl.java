package org.sid.cvthequespringboot.services.FolderServices;

import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.mappers.FileMappers;
import org.sid.cvthequespringboot.repositories.FilesRepository;
import org.sid.cvthequespringboot.repositories.FolderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderServicesImpl implements FolderServices {
    private final FolderRepository folderRepository;
    private final FileMappers fileMappers;
    private final FilesRepository filesRepository;

    public FolderServicesImpl(FolderRepository folderRepository, FileMappers fileMappers, FilesRepository filesRepository) {
        this.folderRepository = folderRepository;
        this.fileMappers = fileMappers;
        this.filesRepository = filesRepository;
    }

    /*public void save(Folder folder) {
        folder.setCreatedAt(new Date());
        folderRepository.save(folder);
    }*/

    @Override
    public FolderDto save(Folder folder) {
        folder.setCreatedAt(new Date());
        Folder newFolder = folderRepository.save(folder);
        return fileMappers.fromFolder(newFolder);
    }

    @Override
    public List<FolderDto> getFolders() {
        List<Folder> folders = folderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<FolderDto> folderDtos = folders.stream()
                .map(fileMappers::fromFolder)
                .collect(Collectors.toList());
        for (FolderDto folder : folderDtos) {
            Long fileCount = filesRepository.countByFolderId(folder.getId());
            folder.setFileCount(fileCount);
        }
        return folderDtos;
    }


}
