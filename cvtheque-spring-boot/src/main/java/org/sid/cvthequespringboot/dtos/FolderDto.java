package org.sid.cvthequespringboot.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FolderDto {
    private Long id;
    private String name;
    private String description;
    private Date createdAt = new Date();
    private Long fileCount;
}
