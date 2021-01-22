package com.kahago.kahagoservice.exception;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hendro yuwono
 */
@Data
public class ErrorMapper {
    private Map<String, List<String>> errors;

    public void addErrors(String key, String error) {
        initializeErrorsIfNull();
        initializeListIfNull(key);

        errors.get(key).add(error);
    }

    private void initializeListIfNull(String key) {
        errors.computeIfAbsent(key, k -> new ArrayList<>());
    }

    private void initializeErrorsIfNull() {
        if (errors == null) errors = new HashMap<>();
    }
}
