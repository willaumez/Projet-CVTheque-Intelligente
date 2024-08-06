package org.sid.cvthequespringboot.web;

import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.services.FileServices;
import org.springframework.core.io.ClassPathResource;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:4200")  // pour autoriser les requêtes provenant de Angular
@CrossOrigin(origins = "*")  // pour autoriser les requêtes provenant de n'importe quelle origine
@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileRestController {



    private FileServices fileServices;

    //private static final String UPLOAD_DIR = "src/main/resources/pdfs/";
    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public void upload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        fileServices.processAndStoreFiles(files);
        System.out.println("File stored successfully");
    }

    //Get all files form the database FileDB
    @GetMapping("/files")
    public List<FileDB> getFiles() {
        return fileServices.getFiles().stream().toList();
    }

/*
    @GetMapping("/read")
    public ResponseEntity<Resource> readFilePdf() {
        Resource pdfFile = new ClassPathResource("files/cvfile.pdf");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=cvfile.pdf");
        return new ResponseEntity<>(pdfFile, headers, HttpStatus.OK);
    }

*/

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
