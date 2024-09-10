package org.sid.cvthequespringboot.web;


import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.services.FolderServices.FolderServices;
import org.sid.cvthequespringboot.services.FolderServices.FolderServicesImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/folder")
@AllArgsConstructor
public class FolderRestController {

    private FolderServicesImpl folderService;

    /*@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void upload(@RequestBody Folder folder) throws IOException {
        // Process and store the folder
        folderService.save(folder);
        System.out.println("Folder stored successfully: " + folder.getName()+ " " + folder.getDescription());
    }*/

    @PostMapping(value = "/save")
    public FolderDto upload(@RequestBody Folder folder) throws IOException {
        // Process and store the folder
        FolderDto folderDto = folderService.save(folder);
        System.out.println("Folder stored successfully: " + folder.getName()+ " " + folder.getDescription());
        return folderDto;
    }

    /*@GetMapping("/folders")
    public List<FolderDto> getFiles() {
        List<FolderDto> folderDtos = folderService.getFolders().stream().toList();
        System.out.println("fileDbDtos: " + folderDtos);
        return folderDtos;
    }*/

    @GetMapping("/folders")
    public List<FolderDto> getFiles(@RequestParam(name = "kw", defaultValue = "") String kw) {
        return folderService.getFolders(kw).stream().toList();
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFiles(
            @RequestParam("fromId") Long fromId,
            @RequestParam("toId") Long toId) {
        System.out.println("==================0 fromId: " + fromId + " toId: " + toId);
        try {
            folderService.transferFiles(fromId, toId);
            return ResponseEntity.ok("Files transferred successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{folderId}")
    public ResponseEntity<?> deleteFolder(@PathVariable("folderId") Long folderId) {
        System.out.println("folderId: " + folderId);

        if (folderId == null) {
            return ResponseEntity.badRequest().body("Folder ID must not be null.");
        }

        try {
            boolean isDeleted = folderService.deleteFolder(folderId);
            if (isDeleted) {
                return ResponseEntity.ok("Folder deleted successfully");
            } else {
                return ResponseEntity.status(404).body("Folder not found or could not be deleted.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting folder: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletes")
    public ResponseEntity<?> deleteFolders(@RequestParam("ids") String folderIds) {
        // Convertir la chaîne de caractères des IDs en une liste
        List<Long> ids = Arrays.stream(folderIds.split(","))
                .map(Long::parseLong)
                .toList();
        try {
            // Boucle pour supprimer chaque dossier
            for (Long id : ids) {
                boolean isDeleted = folderService.deleteFolder(id);
                if (!isDeleted) {
                    return ResponseEntity.status(404).body("Folder with ID " + id + " not found or could not be deleted.");
                }
            }
            return ResponseEntity.ok("Folders deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting folders: " + e.getMessage());
        }
    }

    @GetMapping("/folders/{folderId}")
    public ResponseEntity<?> getFolderById(@PathVariable Long folderId) {
        try {
            Optional<Folder> folder = folderService.findFolderById(folderId);
            if (folder.isPresent()) {
                return ResponseEntity.ok(folder.get());
            } else {
                return ResponseEntity.status(404).body("Folder with ID " + folderId + " not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving folder: " + e.getMessage());
        }
    }





}
