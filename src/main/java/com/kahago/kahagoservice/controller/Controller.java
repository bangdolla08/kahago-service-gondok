package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.model.response.PagingContent;
import org.springframework.data.domain.Page;

/**
 * @author Hendro yuwono
 */
public abstract class Controller {

    protected PagingContent extraPaging(Page page) {
        return PagingContent.builder()
                .positionOfPage(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalOfPages(page.getTotalPages() - 1)
                .build();
    }

    protected PagingContent pagination(Page page) {
        return PagingContent.builder()
                .positionOfPage(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalOfPages(page.getTotalPages())
                .build();
    }
}
