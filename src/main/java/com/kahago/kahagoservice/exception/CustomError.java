package com.kahago.kahagoservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * @author Hendro yuwono
 */

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomError {
    private int code;
    private HttpStatus status;
    private String message;
    private String path;
    private List<String> error;
    private Map<String, List<String>> errors;
}
