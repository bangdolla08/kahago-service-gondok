package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.EditTutorialRequest;
import com.kahago.kahagoservice.model.request.NewTutorialRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.Result;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TutorialBoResponse;
import com.kahago.kahagoservice.service.TutorialService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
@Validated
public class TutorialBocController extends Controller {

    @Autowired
    private TutorialService tutorialService;

    @PostMapping("/boc/tutorial")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("New Tutorial")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Result createNew(@RequestBody @Valid NewTutorialRequest model, Principal authentication) {
        tutorialService.save(model, authentication.getName());

        SaveResponse response = SaveResponse.builder()
                .saveInformation("Berhasil Edit Promo")
                .saveStatus(1)
                .build();
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), response);
    }

    @PutMapping("/boc/tutorial/{id}")
    @ApiOperation("Edit Tutorial")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Result edit(@PathVariable Integer id, @RequestBody EditTutorialRequest request, Principal authentication) {
        tutorialService.saveEdit(authentication.getName(), id, request);

        SaveResponse response = SaveResponse.builder()
                .saveInformation("Berhasil Edit Promo")
                .saveStatus(1)
                .build();
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), response);
    }

    @GetMapping("/boc/tutorial")
    @ApiOperation("List of Tutorial")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<TutorialBoResponse>> fetch(@RequestParam(defaultValue = "WEB") String type, Pageable pageable) {
        Page<TutorialBoResponse> tutorialBoResponses = tutorialService.fetchAll(type, pageable);

        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(tutorialBoResponses),
                tutorialBoResponses.getContent()
        );
    }

    @GetMapping("/boc/tutorial/{id}")
    @ApiOperation("Detail Tutorial")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<TutorialBoResponse> get(@PathVariable Integer id) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), tutorialService.get(id));
    }

    @GetMapping("/boc/tutorial/blast/{id}")
    @ApiOperation("Detail Tutorial")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<TutorialBoResponse> blastData(@PathVariable Integer id) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), tutorialService.get(id));
    }


}
