package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.UserCategoryRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.UserCategoryResponse;
import com.kahago.kahagoservice.service.CategoryUserService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class UserCategoryController {

    @Autowired
    private CategoryUserService categoryUserService;

    @GetMapping("/user/category")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserCategoryResponse>> findAll() {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), categoryUserService.findAll());
    }

    @PostMapping("/user/category")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public void save(@RequestBody @Valid UserCategoryRequest request) {
        categoryUserService.save(request);
    }

    @DeleteMapping("/user/category/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Integer id) {
        categoryUserService.delete(id);
    }
}
