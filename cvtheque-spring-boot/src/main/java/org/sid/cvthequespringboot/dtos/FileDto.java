package org.sid.cvthequespringboot.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FileDto {
    private Long id;
    private String name;
    private String type;
    private Date createdAt = new Date();
    private Long folderId;
    private String folderName;
    private Long pCriteria;
    private Long pKeywords;
    private Long pScoring;
    private boolean evaluation;
}
