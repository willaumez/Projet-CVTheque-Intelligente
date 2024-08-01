package org.sid.cvthequespringboot.web;

import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.services.FileServices;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
