package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.MenuSettingRequest;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.request.SttVendorReq;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.service.SttVendorService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * @author BangDolla08
 * @created 07/10/20-October-2020 @at 10.19
 * @project kahago-service
 */
@BaseController
@ResponseBody
@Validated
public class SttVendorController extends Controller {
    @Autowired
    private SttVendorService sttVendorService;

    @GetMapping("/stt/counting")
    @ApiOperation(value = "Get Counting stt kahago have", response = SttMonitorRes.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<SttMonitorRes>> getTrackingInternal() {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                sttVendorService.getCountLeftStt()
        );
    }

    @ApiOperation(value = "View All Have Stt", response = SttMonitorRes.class)
    @GetMapping("/stt/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ResponseListSttVendor>> getSttData(Integer flag, Integer switcherCode, String origin, String stt, PageHeaderRequest pageHeaderRequest) {
        Page<ResponseListSttVendor> listResp = sttVendorService.getListSttVendor(flag, switcherCode, origin, stt, pageHeaderRequest);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(listResp),
                listResp.getContent());
    }


    @ApiOperation(value = "Set Order Number Menu Parent")
    @PostMapping("/stt/data")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> setOrderNumberParent(@RequestBody RequestSaveList request, Principal principal) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                sttVendorService.saveSttVendor(request.list, principal.getName())
        );
    }


}


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RequestSaveList {
    List<SttVendorReq> list;
}