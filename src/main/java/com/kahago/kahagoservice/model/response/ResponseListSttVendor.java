package com.kahago.kahagoservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author BangDolla08
 * @created 07/10/20-October-2020 @at 12.02
 * @project kahago-service
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseListSttVendor {
    private Integer idSttVendor;
    private Integer switcherCode;
    private String stt;
    private String origin;
    private Integer flag;
}
