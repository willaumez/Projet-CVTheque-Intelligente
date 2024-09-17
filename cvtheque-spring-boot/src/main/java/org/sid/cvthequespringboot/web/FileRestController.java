package org.sid.cvthequespringboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.dtos.*;
import org.sid.cvthequespringboot.entities.Evaluation;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.mappers.FileMappersImpl;
import org.sid.cvthequespringboot.repositories.EvaluationRepository;
import org.sid.cvthequespringboot.repositories.FileStoreRepository;
import org.sid.cvthequespringboot.repositories.FilesRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "http://localhost:4200")  // pour autoriser les requêtes provenant de Angular
//@CrossOrigin(origins = "*")  // pour autoriser les requêtes provenant de n'importe quelle origine
@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileRestController {


    private final ObjectMapper objectMapper;
    private final FileStoreRepository fileStoreRepository;
    private FileServices fileServices;
    private final FilesRepository fileRepository;
    private final EvaluationRepository evaluationRepository;

    private final FileMappersImpl fileMappersImpl;

    //-------------------------------------------------------------------------

    @PostMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("folder") String folderJson) throws IOException {
        FolderDto folderDto = objectMapper.readValue(folderJson, FolderDto.class);
        fileServices.processAndStoreFiles(file, folderDto);
        return ResponseEntity.ok("File stored successfully");
    }

    //-------------------------------------------------------------------------
    //Get all files form the database FileDB
    @GetMapping("/files")
    public List<FileDto> getFiles(@RequestParam(value = "folderId", required = false) Long folderId) {
        List<FileDto> fileDbDtos;

        if (folderId != null) {
            fileDbDtos = fileServices.getFilesByFolderId(folderId).stream().toList();
        } else {
            fileDbDtos = fileServices.getFiles().stream().toList();
        }
        return fileDbDtos;
    }


    @GetMapping("/test")
    public List<FileDto> getFilesAll() {
        List<FileDB> files = fileRepository.findAll();
        return files.stream()
                .map(fileMappersImpl::fromFile)
                .collect(Collectors.toList());
    }

    @GetMapping("/test2")
    public List<EvaluationDto> getEvaluationAll() {
        List<Evaluation> files = evaluationRepository.findAll();
        return files.stream()
                .map(fileMappersImpl::fromEvaluation)
                .collect(Collectors.toList());
    }


    //Read a file from the database FileDB
    @GetMapping("/read/{fileId}")
    public ResponseEntity<Resource> readFilePdf(@PathVariable Long fileId) {
        try {
            File tempFile = fileServices.saveReadFile(fileId);
            System.out.println("tempFile: " + tempFile.toString());
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

    //Upload a file to the database FileDB
    @PutMapping("/update")
    public ResponseEntity<String> updateFile(@RequestBody FileDB fileDB) {
        System.out.println("=========================== fileDB: " + fileDB);
        if (fileDB == null || fileDB.getId() == null) {
            return ResponseEntity.badRequest().body("Invalid file data");
        }

        FileDB updatedFile = fileServices.updateFile(fileDB);
        if (updatedFile != null) {
            return ResponseEntity.ok("File updated successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to update file");
        }
    }

    //Evaluate a file
    @GetMapping("/evaluation/{fileId}")
    public ResponseEntity<EvaluationDto> getEvaluationInfos(@PathVariable Long fileId) {
        EvaluationDto evaluationDto = fileServices.getEvaluationByFileId(fileId);
        if (evaluationDto != null) {
            return ResponseEntity.ok(evaluationDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //Delete Evaluation
    @DeleteMapping("/evaluation/{evaluationId}")
    public ResponseEntity<?> deleteEvaluation(@PathVariable Long evaluationId) {
        boolean deleted = fileServices.deleteEvaluation(evaluationId);
        if (deleted) {
            System.out.println("======================================0Evaluation deleted successfully");
            return ResponseEntity.ok("Evaluation deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete evaluation");
        }
    }

    //Delete all evaluations
    @DeleteMapping("/evaluation/delete")
    public ResponseEntity<?> deleteAllEvaluation(@RequestParam  List<Long> selection) {
        System.out.println("======================================0selection: " + selection);
        boolean deleted = fileServices.deleteEvaluationByFileId(selection);
        if (deleted) {
            return ResponseEntity.ok("All evaluations deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body("Some evaluations could not be deleted.");
        }
    }

    //Stats
    @GetMapping("/getStats")
    public CVStatsDTO getCvCountPerMonth() {
        CVStatsDTO statsDTOS = fileServices.getCvStats();
        //System.out.println("====================== statsDTOS: " + statsDTOS.toString());
        return statsDTOS;
    }
    /*@GetMapping("/count-per-month")
    public List<CVStatsDTO> getCvCountPerMonth() {
        List<CVStatsDTO> statsDTOS = new ArrayList<>();

        // Création de données de test pour plusieurs années
        String[] folders = {"Folder1", "Folder2", "Folder3"};
        int[] years = {2023, 2024, 2025};

        Random random = new Random();

        for (String folder : folders) {
            for (int year : years) {
                for (int month = 1; month <= 12; month++) {
                    long count = random.nextInt(100); // Nombre aléatoire de CVs
                    statsDTOS.add(new CVStatsDTO(folder, month, year, count));
                }
            }
        }

        return statsDTOS;
    }*/





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
