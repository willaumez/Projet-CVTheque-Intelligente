package org.sid.cvthequespringboot.services.AiServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.entities.*;
import org.sid.cvthequespringboot.enums.Status;
import org.sid.cvthequespringboot.mappers.FileMappersImpl;
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
    private final FileMappersImpl fileMappers;

    long waitTime = 4000;

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


    public AiServicesImpl(ChatClient.Builder builder, FileStoreRepository fileStoreRepository, FilesRepository filesRepository, EvaluationRepository evaluationRepository, FileMappersImpl fileMappersImpl) {
        this.chatClient = builder.build();
        this.fileStoreRepository = fileStoreRepository;
        this.filesRepository = filesRepository;
        this.evaluationRepository = evaluationRepository;
        this.fileMappers = fileMappersImpl;
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
    public void selectedCriteria(List<String> selectedCriteria, String jobDescription) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        processSelectedCriteria(documents, selectedCriteria, jobDescription);
    }

    @Transactional
    @Override
    public void selectedCriteria(List<String> selectedCriteria, String jobDescription, Long folderId) {
        List<FileVectorStore> documents = fileStoreRepository.findAllByFolderId(folderId);
        processSelectedCriteria(documents, selectedCriteria, jobDescription);
    }

    //TODO: Implement Keywords
    @Transactional
    @Override
    public void selectedKeywords(List<String> keywords, Long folderId) {
        List<FileVectorStore> documents = fileStoreRepository.findAllByFolderId(folderId);
        processSelectedKeywords(documents, keywords);
    }

    @Transactional
    @Override
    public void selectedKeywords(List<String> keywords) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        processSelectedKeywords(documents, keywords);
    }

    //TODO: Implement scoring
    @Transactional
    @Override
    public void selectedScoring(String jobDescription) {
        List<FileVectorStore> documents = fileStoreRepository.findAll();
        processSelectedScoring(documents, jobDescription);
    }

    @Transactional
    @Override
    public void selectedScoring(String jobDescription, Long folderId) {
        List<FileVectorStore> documents = fileStoreRepository.findAllByFolderId(folderId);
        processSelectedScoring(documents, jobDescription);
    }

    private void processSelectedScoring(List<FileVectorStore> documents, String jobDescription) {
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 2);
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
                Evaluation savedEvaluation = evaluationRepository.save(existingEvaluation);
                fileDB.setEvaluation(savedEvaluation);
                filesRepository.save(fileDB);
                //evaluationRepository.save(existingEvaluation);
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Erreur d'interruption lors de l'attente", ie);
            }
        }
    }

    @Transactional
    public void processSelectedKeywords(List<FileVectorStore> documents, List<String> keywords) {
        // Partitionner la liste des documents en sous-listes de taille 2
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 2);

        // Parcourir chaque partition
        for (List<FileVectorStore> batch : partitionedDocuments) {
            // Construire le contexte à partir des documents
            List<String> context = batch.stream()
                    .map(doc -> "| FileDBId: " + doc.getFileDB().getId() +
                            " | Content: " + doc.getContent())
                    .collect(Collectors.toList());

            // Préparer le prompt pour l'API
            PromptTemplate promptTemplate = new PromptTemplate(promptKeywords);
            Prompt prompt = promptTemplate.create(Map.of(
                    "context", context,
                    "keywords", String.join(", ", keywords)
            ));

            // Créer la ressource à partir du contenu du prompt
            Resource promptResource = new ByteArrayResource(prompt.getContents().getBytes());

            KeywordsEvaluation[] keywordsEvaluation;

            // Appel au service AI avec gestion des exceptions
            try {
                keywordsEvaluation = chatClient.prompt()
                        .system(promptResource)
                        .call().entity(KeywordsEvaluation[].class);
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de l'appel au service AI", e);
            }

            // Parcourir les résultats de l'évaluation des mots-clés
            for (KeywordsEvaluation evaluation : keywordsEvaluation) {
                // Récupérer le fichier correspondant à l'évaluation
                FileDB fileDB = filesRepository.findById(evaluation.fileId())
                        .orElseThrow(() -> new RuntimeException("Fichier introuvable pour l'ID: " + evaluation.fileId()));

                // Chercher si une évaluation existe déjà pour ce fichier
                Evaluation existingEvaluation = evaluationRepository.findByFileDB_Id(fileDB.getId());

                if (existingEvaluation == null) {
                    // Créer une nouvelle évaluation si elle n'existe pas
                    existingEvaluation = new Evaluation();
                    existingEvaluation.setCreatedAt(new Date());
                    existingEvaluation.setFileDB(fileDB);
                    existingEvaluation.setExistWords(new ArrayList<>());
                    existingEvaluation.setNoExistWords(new ArrayList<>());
                } else {
                    // Si l'évaluation existe mais que les listes de mots sont nulles, les initialiser
                    if (existingEvaluation.getExistWords() == null) {
                        existingEvaluation.setExistWords(new ArrayList<>());
                    }
                    if (existingEvaluation.getNoExistWords() == null) {
                        existingEvaluation.setNoExistWords(new ArrayList<>());
                    }
                }

                // Ajouter les mots existants et non existants
                existingEvaluation.getExistWords().addAll(evaluation.existWords());
                existingEvaluation.getNoExistWords().addAll(evaluation.noExistWords());

                // Sauvegarder l'évaluation dans la base de données
                Evaluation savedEvaluation = evaluationRepository.save(existingEvaluation);
                fileDB.setEvaluation(savedEvaluation);
                filesRepository.save(fileDB);
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Erreur d'interruption lors de l'attente", ie);
            }
        }
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
    public void processSelectedCriteria(List<FileVectorStore> documents, List<String> selectedCriteria, String jobDescription) {
        List<List<FileVectorStore>> partitionedDocuments = Lists.partition(documents, 2);
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
            System.out.println("========  criteriaEvaluation: " + Arrays.toString(criteriaEvaluation));
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
                Evaluation savedEvaluation = evaluationRepository.save(existingEvaluation);
                fileDB.setEvaluation(savedEvaluation);
                filesRepository.save(fileDB);
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Erreur d'interruption lors de l'attente", ie);
            }
        }
    }



}
