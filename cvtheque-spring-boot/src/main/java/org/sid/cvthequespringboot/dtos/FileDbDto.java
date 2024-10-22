package org.sid.cvthequespringboot.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter  @Setter
public class FileDbDto {
    private Long id;
    private String name;
    private String type;
    private Date createdAt = new Date();
    private Long folderId;
    private String folderName;
    private List<Criteria> acceptCriteria;
    private List<Criteria> rejectCriteria;
}
