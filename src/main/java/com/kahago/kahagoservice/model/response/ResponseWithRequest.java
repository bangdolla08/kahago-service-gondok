package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author bangd
 */
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResponseWithRequest<Rq,Rs>{
    private String rc;
    private String description;
    private Rq request;
    private Rs data;
    private String timestamp;
    private PagingContent page;
    public ResponseWithRequest(){
        timestamp=new Date().toString();
    }
    public ResponseWithRequest(String rc,String description,Rq request,Rs data,PagingContent page){
        this.rc=rc;
        this.description=description;
        this.request=request;
        this.data=data;
        this.page=page;
        timestamp=new Date().toString();
    }
    public ResponseWithRequest(String rc,String description,Rq request,Rs data){
        this(rc,description,request,data,null);
    }
}
