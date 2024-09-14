package org.sid.cvthequespringboot.mappers;


import org.sid.cvthequespringboot.dtos.*;
import org.sid.cvthequespringboot.entities.*;

import java.util.List;

public interface FileMappers {

    FileDB fromFileDbDto(FileDbDto fileDbDto);
    FileDbDto fromFileDB(FileDB fileDB);

    FileDto fromFile(FileDB fileDB);
    FileDB fromFileDto(FileDto fileDto);

    EvaluationDto fromEvaluation(Evaluation evaluation);

    Evaluation fromEvaluationDto(EvaluationDto evaluationDto);

    List<ScoringDto> fromScoring(List<Scoring> scoring);

    FolderDto fromFolder(Folder folder);
    Folder fromFolderDto(FolderDto folderDto);

    ProfileDto fromProfile(Profile profile);
    Profile fromProfileDto(ProfileDto profileDto);

    CriteriaProfileDto fromCriteria(CriteriaProfile criteriaProfile);
    CriteriaProfile fromCriteriaDTO(CriteriaProfileDto criteriaProfileDto);


}
