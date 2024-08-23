package org.sid.cvthequespringboot.services.AiServices;

import org.sid.cvthequespringboot.dtos.FileAiCriteria;

import java.util.List;

public interface AiServices {
    String generalChat(String question);

    List<FileAiCriteria> selectedCriteria(List<String> selectedCriteria, String jobDescription);
}
