package org.sid.cvthequespringboot.web;


import lombok.AllArgsConstructor;

import org.sid.cvthequespringboot.services.AiServices.AiServices;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class AiRestController {

    private AiServices aiService;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ask(String question) {
        return aiService.generalChat(question);
    }

    @PostMapping("/criteria")
    public ResponseEntity<?> selectedCriteria(
            @RequestParam("selectedCriteria") List<String> selectedCriteria,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "folderId", required = false) Long folderId) {

        try {
            if (folderId != null) {
                aiService.selectedCriteria(selectedCriteria, jobDescription, folderId);
            } else {
                aiService.selectedCriteria(selectedCriteria, jobDescription);
            }
            return ResponseEntity.ok("Criteria done successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error evaluation by Criteria: " + e.getMessage());
        }
    }

    @PostMapping("/keywords")
    public ResponseEntity<?> selectedKeywords(
            @RequestParam("keywords") List<String> keywords,
            @RequestParam(value = "folderId", required = false) Long folderId) {

        try {
            if (folderId != null) {
                aiService.selectedKeywords(keywords, folderId);
            } else {
                aiService.selectedKeywords(keywords);
            }
            return ResponseEntity.ok("Keywords done successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error evaluation by Keywords: " + e.getMessage());
        }
    }
    @PostMapping("/scoring")
    public ResponseEntity<?> selectedScoring(
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "folderId", required = false) Long folderId) {
        try {
            if (folderId != null) {
                aiService.selectedScoring(jobDescription, folderId);
            } else {
                aiService.selectedScoring(jobDescription);
            }
            return ResponseEntity.ok("Scoring done successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error evaluation by Scoring: " + e.getMessage());
        }
    }

}
