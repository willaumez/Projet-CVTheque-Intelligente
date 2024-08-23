package org.sid.cvthequespringboot.web;


import lombok.AllArgsConstructor;
import org.sid.cvthequespringboot.dtos.Criteria;
import org.sid.cvthequespringboot.dtos.FileAiCriteria;
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
        System.out.println("question: " + question);
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

    @PostMapping("/criteria")
    public ResponseEntity<?> selectedCriteria(
            @RequestParam("selectedCriteria") List<String> selectedCriteria,
            @RequestParam("jobDescription") String jobDescription) {
        try {
            List<FileAiCriteria> fileAiCriteriaList = new ArrayList<>();

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
                FileAiCriteria fileAiCriteria = new FileAiCriteria();
                fileAiCriteria.setId((long) i);
                fileAiCriteria.setName("File " + i);
                fileAiCriteria.setType("Type " + i);
                fileAiCriteria.setCreatedAt(new Date());
                fileAiCriteria.setFolderId(100L + i);
                fileAiCriteria.setFolderName("Folder " + i);
                fileAiCriteria.setAcceptCriteria(acceptCriteria);
                fileAiCriteria.setRejectCriteria(rejectCriteria);

                // Ajouter l'objet à la liste
                fileAiCriteriaList.add(fileAiCriteria);
            }
            return ResponseEntity.ok(fileAiCriteriaList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring files: " + e.getMessage());
        }
    }


}
