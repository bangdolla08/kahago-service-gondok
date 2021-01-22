package com.kahago.kahagoservice.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hendro yuwono
 */
@Data
@Builder
public class PagingContent {
    private long totalElements;
    private long totalOfPages;
    private long positionOfPage;
    private long size;
}
