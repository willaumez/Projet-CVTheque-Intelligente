package org.sid.cvthequespringboot.web;


import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.services.FolderServices.FolderServices;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/folder")
@AllArgsConstructor
public class FolderRestController {

    private FolderServices folderService;

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

    @GetMapping("/folders")
    public List<FolderDto> getFiles() {
        List<FolderDto> folderDtos = folderService.getFolders().stream().toList();
        System.out.println("fileDbDtos: " + folderDtos);
        return folderDtos;
    }


}
