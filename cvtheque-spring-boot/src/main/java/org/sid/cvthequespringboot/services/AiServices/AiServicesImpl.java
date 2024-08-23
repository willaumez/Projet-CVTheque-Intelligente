package org.sid.cvthequespringboot.services.AiServices;

import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.FileAiCriteria;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.FileVectorStore;
import org.sid.cvthequespringboot.record.CriteriaEvaluation;
import org.sid.cvthequespringboot.repositories.FileStoreRepository;
import org.sid.cvthequespringboot.repositories.FilesRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiServicesImpl implements AiServices {
    private final ChatClient chatClient;
    private final FileStoreRepository fileStoreRepository;
    private final FilesRepository filesRepository;
    //Prompts
    @Value("classpath:/prompts/prompt-general-chat.st")
    private Resource promptGeneralChat;

    @Value("classpath:/prompts/criteria.st")
    private Resource promptCriteria;


    public AiServicesImpl(ChatClient.Builder builder, FileStoreRepository fileStoreRepository, FilesRepository filesRepository) {
        this.chatClient = builder.build();
        this.fileStoreRepository = fileStoreRepository;
        this.filesRepository = filesRepository;
    }

    @Override
    public String generalChat(String question) {
        //List<Document> documents = vectorStore.similaritySearch(question);
        List<FileVectorStore> documents = fileStoreRepository.findAll();

        // Collectez les contenus et les métadonnées
        List<String> context = documents.stream()
                .map(doc -> "| Metadata: " + doc.getMetadata().toString() + "| FileDBId: " + doc.getFileDB().getId() + " | Content: " + doc.getContent())
                .toList();
        // Affichez les contextes pour débogage
        //System.out.println("context(file); " + context);
        //System.out.println("==========================================documents size: " + context.size());

        // Créez le prompt avec le template
        PromptTemplate promptTemplate = new PromptTemplate(promptGeneralChat);
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));

        // Envoyez le prompt au chatClient et renvoyez la réponse
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    @Override
    @Transactional
    public List<FileAiCriteria> selectedCriteria(List<String> selectedCriteria, String jobDescription) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        List<String> context = documents.stream()
                .map(doc -> "| FileDBId: " + doc.getFileDB().getId() +
                        " | Content: " + doc.getContent())
                .collect(Collectors.toList());
        PromptTemplate promptTemplate = new PromptTemplate(promptCriteria);
        Prompt prompt = promptTemplate.create(Map.of(
                "context", context,
                "selectedCriteria", String.join(", ", selectedCriteria),
                "jobDescription", jobDescription));

        Resource promptResource = new ByteArrayResource(prompt.getContents().getBytes());
        System.out.println("=================================================================");
        CriteriaEvaluation[] criteriaEvaluation;
        try {
            criteriaEvaluation = chatClient.prompt()
                    .system(promptResource)
                    .call().entity(CriteriaEvaluation[].class);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel au service AI", e);
        }
        List<FileAiCriteria> fileAiCriteriaList = new ArrayList<>();
        for (CriteriaEvaluation evaluation : criteriaEvaluation) {
            FileAiCriteria fileAiCriteria = new FileAiCriteria();
            FileDB fileDB = filesRepository.findById(evaluation.fileId())
                    .orElseThrow(() -> new RuntimeException("Fichier introuvable pour l'ID: " + evaluation.fileId()));
            fileAiCriteria.setId(fileDB.getId());
            fileAiCriteria.setName(fileDB.getName());
            fileAiCriteria.setType(fileDB.getType());
            fileAiCriteria.setCreatedAt(fileDB.getCreatedAt());
            fileAiCriteria.setFolderId(fileDB.getFolder().getId());
            fileAiCriteria.setFolderName(fileDB.getFolder().getName());
            fileAiCriteria.setAcceptCriteria(evaluation.acceptCriteria());
            fileAiCriteria.setRejectCriteria(evaluation.rejectCriteria());
            fileAiCriteriaList.add(fileAiCriteria);
        }
        return fileAiCriteriaList;
    }


}
