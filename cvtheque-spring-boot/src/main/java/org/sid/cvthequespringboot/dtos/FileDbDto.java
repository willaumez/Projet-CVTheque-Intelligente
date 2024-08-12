package org.sid.cvthequespringboot.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter  @Setter
public class FileDbDto {
    private Long id;
    private String name;
    private String type;
    private Date createdAt = new Date();
    private Long folderId;
    private String folderName;
}
