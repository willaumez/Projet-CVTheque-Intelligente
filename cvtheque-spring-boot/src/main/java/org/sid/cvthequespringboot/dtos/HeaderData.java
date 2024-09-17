package org.sid.cvthequespringboot.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeaderData {
    private long totalFiles;
    private long totalFolders;
    private long totalProfiles;
    private long totalEvaluated;
    private long totalNotEvaluated;
}
