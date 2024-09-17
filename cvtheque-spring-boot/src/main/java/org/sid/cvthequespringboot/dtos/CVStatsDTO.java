package org.sid.cvthequespringboot.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CVStatsDTO {

    private List<GraphData> graphData;
    private HeaderData headerData;

}