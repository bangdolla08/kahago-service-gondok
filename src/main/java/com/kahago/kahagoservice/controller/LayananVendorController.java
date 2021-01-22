package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.StatusResponse;
import com.kahago.kahagoservice.service.LayananVendorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
@Api(value="Vendor Controller", description="Vendor controller")
public class LayananVendorController extends Controller {

    @Autowired
    private LayananVendorService layananVendorService;

    @GetMapping("/vendor/pos/opensheet")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<StatusResponse> openLayananPos() {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), layananVendorService.openLayanan());
    }

    @GetMapping("/vendor/pos/manifest/create")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<StatusResponse> createManifestPos(@ApiIgnore Authentication authentication) {
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                layananVendorService.createManifest(authentication.getName()));
    }

    @GetMapping("/vendor/pos/manifest/check")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<StatusResponse> checkManifestPos(@ApiIgnore Authentication authentication) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), layananVendorService.checkManifest(authentication.getName()));
    }

    @GetMapping("/vendor/pos/manifest/close")
    public Response<StatusResponse> closeManifestPos(@ApiIgnore Authentication authentication) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), layananVendorService.closeManifest(authentication.getName()));
    }
}
