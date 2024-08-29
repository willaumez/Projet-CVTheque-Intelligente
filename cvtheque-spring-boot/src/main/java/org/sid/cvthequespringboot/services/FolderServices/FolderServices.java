package org.sid.cvthequespringboot.services.FolderServices;

import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.Folder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface FolderServices {
    FolderDto save(Folder folder);
    List<FolderDto> getFolders(String kw);
    void transferFiles(Long fromId, Long toId);

    boolean deleteFolder(Long folderId);

    Optional<Folder> findFolderById(Long folderId);
}
