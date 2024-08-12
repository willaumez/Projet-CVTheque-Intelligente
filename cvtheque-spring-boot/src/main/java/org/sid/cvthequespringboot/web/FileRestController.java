package org.sid.cvthequespringboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.dtos.FileDbDto;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.services.FileServices.FileServices;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:4200")  // pour autoriser les requêtes provenant de Angular
//@CrossOrigin(origins = "*")  // pour autoriser les requêtes provenant de n'importe quelle origine
@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileRestController {


    private final ObjectMapper objectMapper;
    private FileServices fileServices;

    //private static final String UPLOAD_DIR = "src/main/resources/pdfs/";
   /* @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public void upload(@RequestParam("files") List<MultipartFile> files, @RequestParam("folder") String folderJson) throws IOException {
        FolderDto folderDto = objectMapper.readValue(folderJson, FolderDto.class);
        fileServices.processAndStoreFiles(files, folderDto);
        System.out.println("File stored successfully");
    }
*/
    //-------------------------------------------------------------------------

    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("folder") String folderJson) throws IOException {
        // Convertir le JSON en FolderDto
        FolderDto folderDto = objectMapper.readValue(folderJson, FolderDto.class);
        //afficher au format JSON
        System.out.println("folderDto: " + objectMapper.writeValueAsString(folderDto));

        // Appeler le service pour traiter et stocker le fichier
        fileServices.processAndStoreFiles(file, folderDto);

        return ResponseEntity.ok("File stored successfully");
    }


    //-------------------------------------------------------------------------

    //Get all files form the database FileDB
    @GetMapping("/files")
    public List<FileDbDto> getFiles() {
        List<FileDbDto> fileDbDtos = fileServices.getFiles().stream().toList();
        System.out.println("fileDbDtos: " + fileDbDtos);
        return fileDbDtos;
    }

    //Read a file from the database FileDB
    @GetMapping("/read/{fileId}")
    public ResponseEntity<Resource> readFilePdf(@PathVariable Long fileId) {
        System.out.println("fileId: " + fileId);
        try {
            // Charger le fichier depuis la base de données et sauvegarder en tant que fichier temporaire
            File tempFile = fileServices.saveReadFile(fileId);
            System.out.println("tempFile: " + tempFile.toString());

            // Créer une ressource à partir du fichier temporaire
            Resource resource = new UrlResource(tempFile.toURI());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + tempFile.getName());
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Delete all files from the database FileDB and the vectorStore
    @DeleteMapping("/delete/{filesIds}")
    public void deleteFiles(@PathVariable List<Long> filesIds) {
        System.out.println("filesIds: " + filesIds);
        fileServices.deleteFiles(filesIds);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFiles(
            @RequestParam("fileIds") List<Long> fileIds,
            @RequestParam("folderId") Long folderId) {
        try {
            fileServices.transferFiles(fileIds, folderId);
            return ResponseEntity.ok("Files transferred successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }




/*

    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public void upload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        fileServices.processAndStoreFiles(files);
        System.out.println("File stored successfully");
    }

*/


/*
    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public void upload(@RequestParam("files") List<MultipartFile> files) {
        for (MultipartFile file : files) {
            // Log details about the uploaded files
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("File type: " + file.getContentType());
        }
    }*/
/*
    private static final String UPLOAD_DIR = "src/main/resources/pdfs/";

    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public void upload(@RequestParam("files") List<MultipartFile> files) {
        for (MultipartFile file : files) {
            try {
                // Check if the file is a PDF
                if ("application/pdf".equals(file.getContentType())) {
                    // Create the directory if it does not exist
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // Save the file
                    Path filePath = uploadPath.resolve(file.getOriginalFilename());
                    Files.write(filePath, file.getBytes());

                    // Log details about the uploaded file
                    System.out.println("File saved: " + filePath.toString());
                    System.out.println("File name: " + file.getOriginalFilename());
                    System.out.println("File size: " + file.getSize());
                    System.out.println("File type: " + file.getContentType());
                    System.out.println("File bites: " + file.getBytes().length);
                    System.out.println("=========================================================0 ");
                } else {
                    System.out.println("File is not a PDF: " + file.getOriginalFilename());
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save file: " + file.getOriginalFilename());
            }
        }
    }*/

}
