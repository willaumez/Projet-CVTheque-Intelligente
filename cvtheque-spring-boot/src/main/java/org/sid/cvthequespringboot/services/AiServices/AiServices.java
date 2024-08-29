package org.sid.cvthequespringboot.services.AiServices;

import org.sid.cvthequespringboot.dtos.FileAiResults;

import java.util.List;

public interface AiServices {
    String generalChat(String question);

    List<FileAiResults> selectedCriteria(List<String> selectedCriteria, String jobDescription);

    List<FileAiResults> selectedCriteria(List<String> selectedCriteria, String jobDescription, Long folderId);

    List<FileAiResults> selectedKeywords(List<String> keywords, Long folderId);

    List<FileAiResults> selectedKeywords(List<String> keywords);
}
