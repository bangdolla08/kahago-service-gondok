package com.kahago.kahagoservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.security.ResponseAuth;
import com.kahago.kahagoservice.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Hendro yuwono
 */
@Controller
public class SignInController {

    @Autowired
    private UserService userService;

    public String login(String userId, ResponseAuth tokenInfo) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(userService.login(userId, tokenInfo));
    }

    @ApiOperation("login")
    @PostMapping(value = "/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void login(@RequestBody Login login) {
        throw new IllegalStateException("This method shouldn't be called. it's implemented by Spring Security filters. Tricky for swagger documentation :( ");
    }

    @Data
    class Login {
        private String userId;
        private String password;
    }
}
