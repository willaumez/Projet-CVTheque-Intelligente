package org.sid.cvthequespringboot.services;

import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.mappers.FileMappers;
import org.sid.cvthequespringboot.repositories.FolderRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderServices {

    private final FolderRepository folderRepository;
    private final FileMappers fileMappers;

    public FolderServices(FolderRepository folderRepository, FileMappers fileMappers) {
        this.folderRepository = folderRepository;
        this.fileMappers = fileMappers;
    }

    /*public void save(Folder folder) {
        folder.setCreatedAt(new Date());
        folderRepository.save(folder);
    }*/

    public FolderDto save(Folder folder) {
        folder.setCreatedAt(new Date());
        Folder newFolder = folderRepository.save(folder);
        return fileMappers.fromFolder(newFolder);
    }

    public List<FolderDto> getFolders() {
        List<Folder> folders = folderRepository.findAll();
        return folders.stream()
                .map(fileMappers::fromFolder)
                .collect(Collectors.toList());
    }



}
