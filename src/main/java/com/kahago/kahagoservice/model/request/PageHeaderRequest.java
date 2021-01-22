package com.kahago.kahagoservice.model.request;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author bangd ON 12/01/2020
 * @project com.kahago.kahagoservice.model.request
 */
@Data
public class PageHeaderRequest {
    private Integer pageNumber;
    private Integer pageSize;
    private String shortTitle;
    private String shortType;

    public Pageable getPageRequest(){
        if(pageNumber==null)
            pageNumber=0;
        if(pageSize==null)
            pageSize=10;
        return PageRequest.of(pageNumber, pageSize);
    }
}