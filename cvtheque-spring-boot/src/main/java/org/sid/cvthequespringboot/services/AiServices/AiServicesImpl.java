package org.sid.cvthequespringboot.services.AiServices;

import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.FileAiResults;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.FileVectorStore;
import org.sid.cvthequespringboot.record.CriteriaEvaluation;
import org.sid.cvthequespringboot.record.KeywordsEvaluation;
import org.sid.cvthequespringboot.repositories.FileStoreRepository;
import org.sid.cvthequespringboot.repositories.FilesRepository;
import org.springframework.ai.chat.client.ChatClient;
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

    @Value("classpath:/prompts/promptCriteria.st")
    private Resource promptCriteria;

    @Value("classpath:/prompts/promptKeyword.st")
    private Resource promptKeywords;


    public AiServicesImpl(ChatClient.Builder builder, FileStoreRepository fileStoreRepository, FilesRepository filesRepository) {
        this.chatClient = builder.build();
        this.fileStoreRepository = fileStoreRepository;
        this.filesRepository = filesRepository;
    }

    @Override
    public String generalChat(String question) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();

        List<String> context = documents.stream()
                .map(doc -> "| Metadata: " + doc.getMetadata().toString() + "| FileDBId: " + doc.getFileDB().getId() + " | Content: " + doc.getContent())
                .toList();
        PromptTemplate promptTemplate = new PromptTemplate(promptGeneralChat);
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));

        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    @Override
    @Transactional
    public List<FileAiResults> selectedCriteria(List<String> selectedCriteria, String jobDescription) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        return processElectedCriteria(documents, selectedCriteria, jobDescription);
    }

    @Transactional
    @Override
    public List<FileAiResults> selectedCriteria(List<String> selectedCriteria, String jobDescription, Long folderId) {
        List<FileVectorStore> documents = fileStoreRepository.findAllByFolderId(folderId);
        return processElectedCriteria(documents, selectedCriteria, jobDescription);
    }

    @Transactional
    @Override
    public List<FileAiResults> selectedKeywords(List<String> keywords, Long folderId) {
        List<FileVectorStore> documents = fileStoreRepository.findAllByFolderId(folderId);
        return processSelectedKeywords(documents, keywords);
    }

    @Transactional
    @Override
    public List<FileAiResults> selectedKeywords(List<String> keywords) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        return processSelectedKeywords(documents, keywords);
    }
    @Transactional
    public List<FileAiResults> processSelectedKeywords(List<FileVectorStore> documents, List<String> keywords){
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 3);
        List<FileAiResults> fileAiResultsList = new ArrayList<>();
        for (List<FileVectorStore> batch : partitionedDocuments) {
            List<String> context = batch.stream()
                    .map(doc -> "| FileDBId: " + doc.getFileDB().getId() +
                            " | Content: " + doc.getContent())
                    .collect(Collectors.toList());
            PromptTemplate promptTemplate = new PromptTemplate(promptKeywords);
            Prompt prompt = promptTemplate.create(Map.of(
                    "context", context,
                    "keywords", String.join(", ", keywords)));

            Resource promptResource = new ByteArrayResource(prompt.getContents().getBytes());
            KeywordsEvaluation[] keywordsEvaluation;

            try {
                keywordsEvaluation = chatClient.prompt()
                        .system(promptResource)
                        .call().entity(KeywordsEvaluation[].class);
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de l'appel au service AI", e);
            }

            for (KeywordsEvaluation evaluation : keywordsEvaluation) {
                FileAiResults fileAiResults = new FileAiResults();
                FileDB fileDB = filesRepository.findById(evaluation.fileId())
                        .orElseThrow(() -> new RuntimeException("Fichier introuvable pour l'ID: " + evaluation.fileId()));
                fileAiResults.setId(fileDB.getId());
                fileAiResults.setName(fileDB.getName());
                fileAiResults.setType(fileDB.getType());
                fileAiResults.setCreatedAt(fileDB.getCreatedAt());
                fileAiResults.setFolderId(fileDB.getFolder().getId());
                fileAiResults.setFolderName(fileDB.getFolder().getName());
                fileAiResults.setExistWords(evaluation.existWords());
                fileAiResults.setNoExistWords(evaluation.noExistWords());
                fileAiResultsList.add(fileAiResults);
            }
        }
        return fileAiResultsList;
    }

    @Transactional
    public List<FileAiResults> processElectedCriteria(List<FileVectorStore> documents, List<String> selectedCriteria, String jobDescription){
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 3);
        List<FileAiResults> fileAiResultsList = new ArrayList<>();
        for (List<FileVectorStore> batch : partitionedDocuments) {
            List<String> context = batch.stream()
                    .map(doc -> "| FileDBId: " + doc.getFileDB().getId() +
                            " | Content: " + doc.getContent())
                    .collect(Collectors.toList());
            PromptTemplate promptTemplate = new PromptTemplate(promptCriteria);
            Prompt prompt = promptTemplate.create(Map.of(
                    "context", context,
                    "selectedCriteria", String.join(", ", selectedCriteria),
                    "jobDescription", jobDescription));

            Resource promptResource = new ByteArrayResource(prompt.getContents().getBytes());
            CriteriaEvaluation[] criteriaEvaluation;

            try {
                criteriaEvaluation = chatClient.prompt()
                        .system(promptResource)
                        .call().entity(CriteriaEvaluation[].class);
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de l'appel au service AI", e);
            }

            for (CriteriaEvaluation evaluation : criteriaEvaluation) {
                FileAiResults fileAiResults = new FileAiResults();
                FileDB fileDB = filesRepository.findById(evaluation.fileId())
                        .orElseThrow(() -> new RuntimeException("Fichier introuvable pour l'ID: " + evaluation.fileId()));
                fileAiResults.setId(fileDB.getId());
                fileAiResults.setName(fileDB.getName());
                fileAiResults.setType(fileDB.getType());
                fileAiResults.setCreatedAt(fileDB.getCreatedAt());
                fileAiResults.setFolderId(fileDB.getFolder().getId());
                fileAiResults.setFolderName(fileDB.getFolder().getName());
                fileAiResults.setAcceptCriteria(evaluation.acceptCriteria());
                fileAiResults.setRejectCriteria(evaluation.rejectCriteria());
                fileAiResultsList.add(fileAiResults);
            }
        }
        return fileAiResultsList;
    }


}
