package org.sid.cvthequespringboot.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphData {
    private String folderName;
    private int month;
    private int year;
    private long count;
}
