package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.SalesDashboardDtlRequest;
import com.kahago.kahagoservice.model.response.MonitorTransResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SalesDashboardDtlResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.model.response.UserListRes;
import com.kahago.kahagoservice.service.DashboardSalesService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
public class DashboardSalesController extends Controller {
    @Autowired
    private DashboardSalesService dashboardSalesService;

    @GetMapping("/sales/trxGlobal")
    @ApiOperation(value = "Get Total Transaksi By User Category")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<MonitorTransResponse> getTotalTrxGlobalByUserCategory(@RequestParam(defaultValue = "day") String key,
                                                                          @RequestParam(required = false) List<Integer> userCategoryId,
                                                                          @RequestParam(required = false) List<Integer> userCategoryIdNon,
                                                                          @RequestParam(required = false) String statusUser,
                                                                          @RequestParam(required = false) String userSales) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                dashboardSalesService.getAllTrxByUserCategoryAndDate(userCategoryId, userCategoryIdNon, key, statusUser, userSales)
        );
    }

    @GetMapping("/sales/averageTrx")
    @ApiOperation(value = "Get Average Transaksi Bulan lalu")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<TotalTrxResponse> getAverageTrxPastMonth(@RequestParam(defaultValue = "all") String type) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                dashboardSalesService.getAverageTrxPerPastMonth(type)
        );
    }

    @GetMapping("/sales/totalUser")
    @ApiOperation(value = "Get Total New User")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<TotalTrxResponse> getTotalNewUserByMinTrxDate(@RequestParam(required = false) String key,
                                                                  @RequestParam(required = false) String userSales) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                dashboardSalesService.getTotalUserByMinTrxDateAndRefNum(key, userSales)
        );
    }

    @GetMapping("/sales/detailDashboard")
    @ApiOperation(value = "Get Detail Dashboard Sales")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<SalesDashboardDtlResponse>> getDetailDashboardSales(SalesDashboardDtlRequest request) {
        Page<SalesDashboardDtlResponse> lDtlSales = dashboardSalesService.getDetailDashboardSales(request);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(lDtlSales),
                lDtlSales.getContent()
        );
    }

    @GetMapping("/sales/detailSales")
    @ApiOperation(value = "Get Detail for Tab Sales")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<SalesDashboardDtlResponse>> getDetailForTabSales(SalesDashboardDtlRequest request) {
        Page<SalesDashboardDtlResponse> lDtlForSales = dashboardSalesService.getDetailDashboardSalesForTabSales(request);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(lDtlForSales),
                lDtlForSales.getContent()
        );
    }

    @GetMapping("/sales/listSales")
    @ApiOperation(value = "Get All List Name Sales")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserListRes>> getListNameOfSales(Principal principal) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                dashboardSalesService.getListUserSales(principal.getName())
        );
    }
}
