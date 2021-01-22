package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Hendro yuwono
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Response<T> implements Result {
    private String rc;
    private String description;
    private PagingContent page;
    private T data;

    public Response(String rc, String description, T data) {
        this(rc, description, null, data);
    }

    public Response(String rc, String description) {
        this(rc, description, null, null);
    }
}
