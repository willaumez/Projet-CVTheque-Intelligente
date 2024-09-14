package org.sid.cvthequespringboot.services.AiServices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.FileAiResults;
import org.sid.cvthequespringboot.entities.*;
import org.sid.cvthequespringboot.enums.Status;
import org.sid.cvthequespringboot.record.CriteriaEvaluation;
import org.sid.cvthequespringboot.record.KeywordsEvaluation;
import org.sid.cvthequespringboot.record.ScoringEvaluation;
import org.sid.cvthequespringboot.repositories.EvaluationRepository;
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
    private final EvaluationRepository evaluationRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    //Prompts
    @Value("classpath:/prompts/prompt-general-chat.st")
    private Resource promptGeneralChat;

    @Value("classpath:/prompts/promptCriteria.st")
    private Resource promptCriteria;

    @Value("classpath:/prompts/promptKeyword.st")
    private Resource promptKeywords;

    @Value("classpath:/prompts/promptScoring.st")
    private Resource promptScoring;


    public AiServicesImpl(ChatClient.Builder builder, FileStoreRepository fileStoreRepository, FilesRepository filesRepository, EvaluationRepository evaluationRepository) {
        this.chatClient = builder.build();
        this.fileStoreRepository = fileStoreRepository;
        this.filesRepository = filesRepository;
        this.evaluationRepository = evaluationRepository;
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

    //TODO: Implement Criteria
    @Override
    @Transactional
    public List<FileAiResults> selectedCriteria(List<String> selectedCriteria, String jobDescription) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        return processSelectedCriteria(documents, selectedCriteria, jobDescription);
    }

    @Transactional
    @Override
    public List<FileAiResults> selectedCriteria(List<String> selectedCriteria, String jobDescription, Long folderId) {
        List<FileVectorStore> documents = fileStoreRepository.findAllByFolderId(folderId);
        return processSelectedCriteria(documents, selectedCriteria, jobDescription);
    }

    //TODO: Implement Keywords
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

    //TODO: Implement scoring
    @Transactional
    @Override
    public List<FileAiResults> selectedScoring(String jobDescription) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        return processSelectedScoring(documents, jobDescription);
    }

    @Transactional
    @Override
    public List<FileAiResults> selectedScoring(String jobDescription, Long folderId) {
        List<FileVectorStore> documents = fileStoreRepository.findAllByFolderId(folderId);
        return processSelectedScoring(documents, jobDescription);
    }

    private List<FileAiResults> processSelectedScoring(List<FileVectorStore> documents, String jobDescription) {
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 2);
        List<FileAiResults> fileAiResultsList = new ArrayList<>();
        for (List<FileVectorStore> batch : partitionedDocuments) {
            List<String> context = batch.stream()
                    .map(doc -> "| FileDBId: " + doc.getFileDB().getId() +
                            " | Content: " + doc.getContent())
                    .toList();
            PromptTemplate promptTemplate = new PromptTemplate(promptScoring);
            Prompt prompt = promptTemplate.create(Map.of(
                    "jobDescription", jobDescription,
                    "context", context));
            Resource promptResource = new ByteArrayResource(prompt.getContents().getBytes());
            ScoringEvaluation[] scoringEvaluations;
            try {
                scoringEvaluations = chatClient.prompt()
                        .system(promptResource)
                        .call().entity(ScoringEvaluation[].class);
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de l'appel au service AI", e);
            }
            for (ScoringEvaluation evaluation : scoringEvaluations) {
                FileAiResults fileAiResults = new FileAiResults();
                FileDB fileDB = filesRepository.findById(evaluation.fileId())
                        .orElseThrow(() -> new RuntimeException("Fichier introuvable pour l'ID: " + evaluation.fileId()));

                // Chercher si une évaluation existe déjà pour ce fichier
                Evaluation existingEvaluation = evaluationRepository.findByFileDB_Id(fileDB.getId());
                if (existingEvaluation == null) {
                    existingEvaluation = new Evaluation();
                    existingEvaluation.setCreatedAt(new Date());
                    existingEvaluation.setFileDB(fileDB);
                }
                Scoring newScoring = Scoring.builder()
                        .profile(evaluation.scoringDto().getProfile())
                        .score(evaluation.scoringDto().getScore())
                        .message(evaluation.scoringDto().getMessage())
                        .createdAt(new Date())
                        .build();
                // Ajouter le nouveau Scoring à la liste existante
                if (existingEvaluation.getScoring() == null) {
                    existingEvaluation.setScoring(new ArrayList<>());
                }
                existingEvaluation.getScoring().add(newScoring);
                evaluationRepository.save(existingEvaluation);

                // Remplir les résultats

                fileAiResults.setId(fileDB.getId());
                fileAiResults.setName(fileDB.getName());
                fileAiResults.setType(fileDB.getType());
                fileAiResults.setCreatedAt(fileDB.getCreatedAt());
                fileAiResults.setFolderId(fileDB.getFolder().getId());
                fileAiResults.setFolderName(fileDB.getFolder().getName());
                fileAiResults.setScoringDto(evaluation.scoringDto());
                fileAiResultsList.add(fileAiResults);
            }
        }
        return fileAiResultsList;
    }

    @Transactional
    public List<FileAiResults> processSelectedKeywords(List<FileVectorStore> documents, List<String> keywords){
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 2);
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

                // Chercher si une évaluation existe déjà pour ce fichier
                Evaluation existingEvaluation = evaluationRepository.findByFileDB_Id(fileDB.getId());

                if (existingEvaluation == null) {
                    existingEvaluation = new Evaluation();
                    existingEvaluation.setCreatedAt(new Date());
                    existingEvaluation.setFileDB(fileDB);
                    existingEvaluation.setExistWords(new ArrayList<>());
                    existingEvaluation.setNoExistWords(new ArrayList<>());
                } else {
                    if (existingEvaluation.getExistWords() == null) {
                        existingEvaluation.setExistWords(new ArrayList<>());
                    }
                    if (existingEvaluation.getNoExistWords() == null) {
                        existingEvaluation.setNoExistWords(new ArrayList<>());
                    }
                    existingEvaluation.getExistWords().addAll(evaluation.existWords());
                    existingEvaluation.getNoExistWords().addAll(evaluation.noExistWords());
                }
                evaluationRepository.save(existingEvaluation);


                // Remplir les résultats
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

    /*@Transactional
    public List<FileAiResults> processElectedCriteria(List<FileVectorStore> documents, List<String> selectedCriteria, String jobDescription){
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 2);
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
    }*/
    @Transactional
    public List<FileAiResults> processSelectedCriteria(List<FileVectorStore> documents, List<String> selectedCriteria, String jobDescription) {
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 2);
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
                FileDB fileDB = filesRepository.findById(evaluation.fileId())
                        .orElseThrow(() -> new RuntimeException("Fichier introuvable pour l'ID: " + evaluation.fileId()));

                Evaluation existingEvaluation = evaluationRepository.findByFileDB_Id(fileDB.getId());
                if (existingEvaluation == null) {
                    existingEvaluation = new Evaluation();
                    existingEvaluation.setCreatedAt(new Date());
                    existingEvaluation.setFileDB(fileDB);
                }
                List<CriteriaEval> currentCriteriaEvals = existingEvaluation.getAcceptRejectCriteria();
                if (currentCriteriaEvals == null) {
                    currentCriteriaEvals = new ArrayList<>();
                }
                currentCriteriaEvals.addAll(evaluation.acceptCriteria().stream()
                        .map(c -> {
                            CriteriaEval criteriaEval = new CriteriaEval();
                            criteriaEval.setCreatedAt(new Date());
                            criteriaEval.setName(c.getName());
                            criteriaEval.setMessage(c.getMessage());
                            criteriaEval.setStatus(Status.ACCEPTED);
                            return criteriaEval;
                        })
                        .toList());
                currentCriteriaEvals.addAll(evaluation.rejectCriteria().stream()
                        .map(c -> {
                            CriteriaEval criteriaEval = new CriteriaEval();
                            criteriaEval.setCreatedAt(new Date());
                            criteriaEval.setName(c.getName());
                            criteriaEval.setMessage(c.getMessage());
                            criteriaEval.setStatus(Status.REJECTED);
                            return criteriaEval;
                        })
                        .toList());
                existingEvaluation.setAcceptRejectCriteria(currentCriteriaEvals);
                evaluationRepository.save(existingEvaluation);
                /*
                // Sauvegarder l'évaluation dans la base de données
                String existingEvaluationJson = null;
                try {
                    existingEvaluationJson = objectMapper.writeValueAsString(existingEvaluation.getAcceptRejectCriteria());
                    System.out.println("============================ existingEvaluation: " + existingEvaluationJson);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }*/
                // Remplir les résultats
                FileAiResults fileAiResults = new FileAiResults();
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
