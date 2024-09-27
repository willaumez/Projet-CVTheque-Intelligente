package org.sid.cvthequespringboot.mappers;

import org.sid.cvthequespringboot.dtos.*;
import org.sid.cvthequespringboot.entities.*;
import org.sid.cvthequespringboot.enums.Status;
import org.sid.cvthequespringboot.repositories.EvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileMappersImpl implements FileMappers {
    private final EvaluationRepository evaluationRepository;

    public FileMappersImpl(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    @Override
    public FileDB fromFileDbDto(FileDbDto fileDbDto) {
        FileDB fileDB = new FileDB();
        fileDB.setId(fileDbDto.getId());
        fileDB.setName(fileDbDto.getName());
        fileDB.setType(fileDbDto.getType());
        fileDB.setCreatedAt(fileDbDto.getCreatedAt());
        if (fileDbDto.getFolderId() != null || fileDbDto.getFolderName() != null) {
            Folder folder = new Folder();
            folder.setId(fileDbDto.getFolderId());
            folder.setName(fileDbDto.getFolderName());
            fileDB.setFolder(folder);
        } else {
            fileDB.setFolder(null);
        }
        return fileDB;
    }

    @Override
    public FileDbDto fromFileDB(FileDB fileDB) {
        FileDbDto fileDbDto = new FileDbDto();
        fileDbDto.setId(fileDB.getId());
        fileDbDto.setName(fileDB.getName());
        fileDbDto.setType(fileDB.getType());
        fileDbDto.setCreatedAt(fileDB.getCreatedAt());
        if (fileDB.getFolder() != null) {
            fileDbDto.setFolderId(fileDB.getFolder().getId());
            fileDbDto.setFolderName(fileDB.getFolder().getName());
        } else {
            fileDbDto.setFolderId(null);
            fileDbDto.setFolderName(null);
        }

        return fileDbDto;
    }

    @Override
    public FileDto fromFile(FileDB fileDB) {
        FileDto fileDto = new FileDto();
        fileDto.setId(fileDB.getId());
        fileDto.setName(fileDB.getName());
        fileDto.setType(fileDB.getType());
        fileDto.setCreatedAt(fileDB.getCreatedAt());
        if (fileDB.getFolder() != null) {
            fileDto.setFolderId(fileDB.getFolder().getId());
            fileDto.setFolderName(fileDB.getFolder().getName());
        } else {
            fileDto.setFolderId(null);
            fileDto.setFolderName(null);
        }
        Evaluation evaluation = evaluationRepository.findByFileDB_Id(fileDB.getId());

        if (evaluation != null) {
            fileDto.setEvaluation(true);
            EvaluationDto evaluationDto = fromEvaluation(evaluation);

            // Vérification et calcul du pourcentage pour AcceptCriteria
            if (evaluationDto.getAcceptCriteria() != null && evaluationDto.getRejectCriteria() != null) {
                int acceptCriteriaSize = evaluationDto.getAcceptCriteria().size();
                int rejectCriteriaSize = evaluationDto.getRejectCriteria().size();
                int totalCriteriaSize = acceptCriteriaSize + rejectCriteriaSize;
                if (totalCriteriaSize > 0) {
                    int pCriteria = (acceptCriteriaSize * 100 / totalCriteriaSize);
                    fileDto.setPCriteria((long) pCriteria);
                } else {
                    fileDto.setPCriteria(null);
                }
            } else {
                fileDto.setPCriteria(null);
            }

            if (evaluationDto.getScoring() != null && !evaluationDto.getScoring().isEmpty() && evaluationDto.getScoring().getLast().getScore() != null) {
                int scoringSize = evaluationDto.getScoring().size();
                if (scoringSize > 0) {
                    fileDto.setPScoring(evaluationDto.getScoring().getLast().getScore());
                } else {
                    fileDto.setPScoring(null);
                }
            } else {
                fileDto.setPScoring(null);
            }

            if (evaluationDto.getExistWords() != null && evaluationDto.getNoExistWords() != null) {
                int existWordsSize = evaluationDto.getExistWords().size();
                int noExistWordsSize = evaluationDto.getNoExistWords().size();
                int totalWordsSize = existWordsSize + noExistWordsSize;
                if (totalWordsSize > 0) {
                    int pKeywords = (existWordsSize * 100 / totalWordsSize);
                    fileDto.setPKeywords((long) pKeywords);
                } else {
                    fileDto.setPKeywords(null);
                }
            } else {
                fileDto.setPKeywords(null);
            }
        } else {
            fileDto.setEvaluation(false);
        }

        return fileDto;
    }

    @Override
    public FileDB fromFileDto(FileDto fileDto) {
        return null;
    }

    //=========================================================================================================
    @Override
    public EvaluationDto fromEvaluation(Evaluation evaluation) {
        EvaluationDto evaluationDto = new EvaluationDto();
        evaluationDto.setId(evaluation.getId());
        List<CriteriaEval> allCriteria = evaluation.getAcceptRejectCriteria();
        if (allCriteria != null) {
            List<CriteriaEval> acceptedCriteria = allCriteria.stream()
                    .filter(criteria -> criteria != null && Status.ACCEPTED.equals(criteria.getStatus()))
                    .collect(Collectors.toList());
            List<CriteriaEval> rejectedCriteria = allCriteria.stream()
                    .filter(criteria -> criteria != null && Status.REJECTED.equals(criteria.getStatus()))
                    .collect(Collectors.toList());

            evaluationDto.setAcceptCriteria(acceptedCriteria);
            evaluationDto.setRejectCriteria(rejectedCriteria);
        } else {
            evaluationDto.setAcceptCriteria(Collections.emptyList());
            evaluationDto.setRejectCriteria(Collections.emptyList());
        }

        evaluationDto.setCreatedAt(evaluation.getCreatedAt());
        evaluationDto.setExistWords(evaluation.getExistWords() != null ? evaluation.getExistWords() : Collections.emptyList());
        evaluationDto.setNoExistWords(evaluation.getNoExistWords() != null ? evaluation.getNoExistWords() : Collections.emptyList());
        evaluationDto.setScoring(fromScoring(evaluation.getScoring() != null ? evaluation.getScoring() : Collections.emptyList()));

        return evaluationDto;
    }

    @Override
    public Evaluation fromEvaluationDto(EvaluationDto evaluationDto) {
        return null;
    }

    @Override
    public List<ScoringDto> fromScoring(List<Scoring> scoring) {
        List<ScoringDto> scoringDto = new ArrayList<>();
        for (Scoring score : scoring) {
            ScoringDto scoringDto1 = new ScoringDto();
            scoringDto1.setProfile(score.getProfile());
            scoringDto1.setCreatedAt(score.getCreatedAt());
            scoringDto1.setScore(score.getScore());
            scoringDto1.setMessage(score.getMessage());
            scoringDto.add(scoringDto1);
        }
        return scoringDto;
    }
    //=========================================================================================================

    @Override
    public FolderDto fromFolder(Folder folder) {
        if (folder == null) {
            return null;
        }
        FolderDto folderDto = new FolderDto();
        folderDto.setId(folder.getId());
        folderDto.setName(folder.getName());
        folderDto.setDescription(folder.getDescription());
        folderDto.setCreatedAt(folder.getCreatedAt());
        return folderDto;
    }

    @Override
    public Folder fromFolderDto(FolderDto folderDto) {
        if (folderDto  == null) {
            return null;
        }
        Folder folder = new Folder();
        folder.setId(folderDto.getId());
        folder.setName(folderDto.getName());
        folder.setDescription(folderDto.getDescription());
        folder.setCreatedAt(folderDto.getCreatedAt());
        return folder;
    }

    @Override
    public ProfileDto fromProfile(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(profile.getId());
        profileDto.setName(profile.getName());
        profileDto.setDescription(profile.getDescription());
        profileDto.setCreatedAt(profile.getCreatedAt());

        return profileDto;
    }

    @Override
    public Profile fromProfileDto(ProfileDto profileDto) {
        if (profileDto == null) {
            return null;
        }

        Profile profile = new Profile();
        profile.setId(profileDto.getId());
        profile.setName(profileDto.getName());
        profile.setDescription(profileDto.getDescription());
        profile.setCreatedAt(profileDto.getCreatedAt());

        return profile;
    }

    @Override
    public CriteriaProfileDto fromCriteria(CriteriaProfile criteriaProfile) {
        if (criteriaProfile == null) {
            return null;
        }

        CriteriaProfileDto criteriaProfileDto = new CriteriaProfileDto();
        criteriaProfileDto.setId(criteriaProfile.getId());
        criteriaProfileDto.setDescription(criteriaProfile.getDescription());
        criteriaProfileDto.setCreatedAt(criteriaProfile.getCreatedAt());

        return criteriaProfileDto;
    }

    @Override
    public CriteriaProfile fromCriteriaDTO(CriteriaProfileDto criteriaProfileDto) {
        if (criteriaProfileDto == null) {
            return null;
        }
        CriteriaProfile criteriaProfile = new CriteriaProfile();
        criteriaProfile.setDescription(criteriaProfileDto.getDescription());
        criteriaProfile.setCreatedAt(criteriaProfileDto.getCreatedAt());

        Profile profile = new Profile();
        profile.setId(criteriaProfileDto.getId());
        criteriaProfile.setProfile(profile);

        return criteriaProfile;
    }


}
