package org.sid.cvthequespringboot.services.FolderServices;

import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.FileVectorStore;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.mappers.FileMappers;
import org.sid.cvthequespringboot.repositories.FileStoreRepository;
import org.sid.cvthequespringboot.repositories.FilesRepository;
import org.sid.cvthequespringboot.repositories.FolderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class FolderServicesImpl implements FolderServices {
    private final FolderRepository folderRepository;
    private final FileMappers fileMappers;
    private final FilesRepository filesRepository;
    private final FileStoreRepository fileStoreRepository;

    public FolderServicesImpl(FolderRepository folderRepository, FileMappers fileMappers, FilesRepository filesRepository, FileStoreRepository fileStoreRepository) {
        this.folderRepository = folderRepository;
        this.fileMappers = fileMappers;
        this.filesRepository = filesRepository;
        this.fileStoreRepository = fileStoreRepository;
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

  /*  @Override
    public List<FolderDto> getFolders(String kw) {
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
*/

    @Override
    public List<FolderDto> getFolders(String kw) {
        List<Folder> folders;

        if (kw != null && !kw.isEmpty()) {
            // Utiliser la nouvelle méthode pour rechercher et trier les dossiers
            folders = folderRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(kw);
        } else {
            // Si aucun mot-clé n'est fourni, récupérer tous les dossiers et les trier par date de création
            folders = folderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        // Mapper les dossiers en FolderDto
        List<FolderDto> folderDtos = folders.stream()
                .map(fileMappers::fromFolder)
                .collect(Collectors.toList());

        // Compter les fichiers dans chaque dossier
        for (FolderDto folder : folderDtos) {
            Long fileCount = filesRepository.countByFolderId(folder.getId());
            folder.setFileCount(fileCount);
        }

        return folderDtos;
    }

    @Transactional
    @Override
    public void transferFiles(Long fromId, Long toId) {
        // Vérifier si le dossier cible existe
        List<FileDB> fileDBList = filesRepository.findAllByFolderId(fromId);
        if (fileDBList.isEmpty()) {
            throw new IllegalArgumentException("No files found in the source folder");
        }
        Folder folder = folderRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));

        for (FileDB fileDB : fileDBList) {
            fileDB.setFolder(folder);
            filesRepository.save(fileDB);
        }
        for (FileDB fileDB : fileDBList) {
            FileVectorStore fileVectorStore = fileStoreRepository.findByFileDBId(fileDB.getId());
            fileVectorStore.setFolder(folder);
            fileStoreRepository.save(fileVectorStore);
        }
        System.out.println("Files transferred successfully");
    }

    @Transactional
    @Override
    public boolean deleteFolder(Long folderId) {
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        if (folderOptional.isPresent()) {
            Folder folder = folderOptional.get();
            filesRepository.deleteAllByFolderId(folder.getId());
            fileStoreRepository.deleteAllByFolderId(folder.getId());
            folderRepository.delete(folder);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<Folder> findFolderById(Long folderId) {
        return folderRepository.findById(folderId);
    }


}
