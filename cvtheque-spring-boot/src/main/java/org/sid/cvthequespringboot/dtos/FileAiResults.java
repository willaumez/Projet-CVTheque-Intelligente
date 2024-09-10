package org.sid.cvthequespringboot.dtos;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileAiResults {
    private Long id;
    private String name;
    private String type;
    private Date createdAt = new Date();
    private Long folderId;
    private String folderName;
    private List<Criteria> acceptCriteria;
    private List<Criteria> rejectCriteria;
    private List<String> existWords;
    private List<String> noExistWords;
    private Scoring scoring;
}