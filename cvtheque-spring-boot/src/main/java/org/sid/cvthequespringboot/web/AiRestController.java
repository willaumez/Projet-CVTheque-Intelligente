package org.sid.cvthequespringboot.web;


import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.dtos.Criteria;
import org.sid.cvthequespringboot.dtos.FileAiResults;
import org.sid.cvthequespringboot.services.AiServices.AiServices;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@CrossOrigin(origins = "*")  // pour autoriser les requêtes provenant de n'importe quelle origine
@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class AiRestController {

    private AiServices aiService;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ask(String question) {
        return aiService.generalChat(question);
    }

  /*  @PostMapping("/criteria")
    public ResponseEntity<?> selectedCriteria(
            @RequestParam("selectedCriteria") List<String> selectedCriteria,
            @RequestParam("jobDescription") String jobDescription) {
        try {
            List<FileAiCriteria> fileAiCriteria = aiService.selectedCriteria(selectedCriteria, jobDescription);
            return ResponseEntity.ok(fileAiCriteria);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }*/

    /*@PostMapping("/criteria")
    public ResponseEntity<?> selectedCriteria(
            @RequestParam("selectedCriteria") List<String> selectedCriteria,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "folderId", required = false) Long folderId) {

        try {
            List<FileAiCriteria> fileAiCriteria;
            if (folderId != null) {
                fileAiCriteria = aiService.selectedCriteria(selectedCriteria, jobDescription, folderId);
            } else {
                fileAiCriteria = aiService.selectedCriteria(selectedCriteria, jobDescription);
            }

            return ResponseEntity.ok(fileAiCriteria);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }*/

    @PostMapping("/criteria")
    public ResponseEntity<?> selectedCriteria(
            @RequestParam("selectedCriteria") List<String> selectedCriteria,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "folderId", required = false) Long folderId) { // Ajout de folderId facultatif

        try {
            List<FileAiResults> fileAiResultsList = new ArrayList<>();

            // Boucle pour initialiser 10 objets FileAiCriteria
            for (int i = 1; i <= 10; i++) {
                // Créer des critères d'acceptation
                List<Criteria> acceptCriteria = new ArrayList<>();
                acceptCriteria.add(new Criteria("Criteria " + i + "-1", "Accept message for criteria " + i + "-1"));
                acceptCriteria.add(new Criteria("Criteria " + i + "-2", "Accept message for criteria " + i + "-2"));

                // Créer des critères de rejet
                List<Criteria> rejectCriteria = new ArrayList<>();
                rejectCriteria.add(new Criteria("Criteria " + i + "-A", "Reject message for criteria " + i + "-A"));
                rejectCriteria.add(new Criteria("Criteria " + i + "-B", "Reject message for criteria " + i + "-B"));

                // Initialiser un objet FileAiCriteria
                FileAiResults fileAiResults = new FileAiResults();
                fileAiResults.setId((long) i);
                fileAiResults.setName("File " + i);
                fileAiResults.setType("Type " + i);
                fileAiResults.setCreatedAt(new Date());
                fileAiResults.setFolderId(100L + i);
                fileAiResults.setFolderName("Folder " + i);
                fileAiResults.setAcceptCriteria(acceptCriteria);
                fileAiResults.setRejectCriteria(rejectCriteria);

                // Ajouter l'objet à la liste
                fileAiResultsList.add(fileAiResults);
            }
            return ResponseEntity.ok(fileAiResultsList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }
/*
    @PostMapping("/keywords")
    public ResponseEntity<?> selectedKeywords(
            @RequestParam("keywords") List<String> keywords,
            @RequestParam(value = "folderId", required = false) Long folderId) {

        try {
            List<FileAiResults> fileAiCriteria;
            if (folderId != null) {
                fileAiCriteria = aiService.selectedKeywords(keywords, folderId);
            } else {
                fileAiCriteria = aiService.selectedKeywords(keywords);
            }

            return ResponseEntity.ok(fileAiCriteria);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }*/

    @PostMapping("/keywords")
    public ResponseEntity<?> selectedKeywords(
            @RequestParam("keywords") List<String> keywords,
            @RequestParam(value = "folderId", required = false) Long folderId) {

        try {
            List<FileAiResults> fileAiResultsList = new ArrayList<>();

            // Boucle pour initialiser 10 objets FileAiCriteria
            for (int i = 1; i <= 10; i++) {
                // Créer des critères d'acceptation
                List<String> existWords = List.of(
                        "acceptKeyword 1",
                        "acceptKeyword 2",
                        "acceptKeyword 3",
                        "acceptKeyword 4",
                        "acceptKeyword 5",
                        "acceptKeyword 6",
                        "acceptKeyword 7",
                        "acceptKeyword 8",
                        "acceptKeyword 9",
                        "acceptKeyword 10",
                        "acceptKeyword 11"
                );
                List<String> noExistWords = List.of(
                        "noExistWords 1",
                        "noExistWords 2",
                        "noExistWords 3",
                        "noExistWords 4",
                        "noExistWords 5",
                        "noExistWords 6"
                );
                // Initialiser un objet FileAiCriteria
                FileAiResults fileAiResults = new FileAiResults();
                fileAiResults.setId((long) i);
                fileAiResults.setName("File " + i);
                fileAiResults.setType("Type " + i);
                fileAiResults.setCreatedAt(new Date());
                fileAiResults.setFolderId(100L + i);
                fileAiResults.setFolderName("Folder " + i);
                fileAiResults.setExistWords(existWords);
                fileAiResults.setNoExistWords(noExistWords);

                // Ajouter l'objet à la liste
                fileAiResultsList.add(fileAiResults);
            }
            return ResponseEntity.ok(fileAiResultsList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }


}
