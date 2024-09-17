package org.sid.cvthequespringboot.services.AiServices;

import java.util.List;

public interface AiServices {
    String generalChat(String question);

    void selectedCriteria(List<String> selectedCriteria, String jobDescription);
    void selectedCriteria(List<String> selectedCriteria, String jobDescription, Long folderId);

    void selectedKeywords(List<String> keywords, Long folderId);
    void selectedKeywords(List<String> keywords);

    void selectedScoring(String jobDescription);
    void selectedScoring(String jobDescription, Long folderId);
}
