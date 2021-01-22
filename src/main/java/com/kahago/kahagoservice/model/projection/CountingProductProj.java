package com.kahago.kahagoservice.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hendro yuwono
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountingProductProj {
    private Long jumlah;
    private Long weight;
    private String name;
    private String operatorSw;
    private String kantongPos;
}
