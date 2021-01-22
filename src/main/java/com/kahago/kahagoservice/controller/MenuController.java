package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import com.kahago.kahagoservice.model.response.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.MenuParentReq;
import com.kahago.kahagoservice.model.request.MenuSettingRequest;
import com.kahago.kahagoservice.service.MenuService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@BaseController
@ResponseBody
@Api(value = "Menu Management System", description = "View and change data menu that have been stored")
public class MenuController extends Controller {
    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "Show List Menu ")
    @GetMapping("/menu/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuDetails>> getListMenu(String userid) {
        log.info("===> Menu List <===");
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.findAllMenu(userid)
        );
    }

    @ApiOperation(value = "Show Auth Menu ")
    @GetMapping("/menu/auth")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<MenuList> listMenuOut(String userid, Integer idMenu) {
        log.info("===> Menu List <===");
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getPrivelage(userid, idMenu)
        );
    }

    @ApiOperation(value = "Tambah Menu ")
    @PostMapping("/menu/header/{action}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<MenuParentReq, MenuParentList> doSaveMenu(@RequestBody MenuParentReq menu) {
        return new ResponseWithRequest<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menu,
                menuService.doSave(menu));
    }

    @ApiOperation(value = "List Menu Header")
    @GetMapping("/menu/header/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuParentList>> getListMenuHeader() {

        return new Response<List<MenuParentList>>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getMenuHeader()
        );
    }

    @ApiOperation(value = "List of Menu")
    @GetMapping("/menu/list/all")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuTitle>> getAllMenu() {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getAllMenuPermission()
        );
    }

    @ApiOperation("List of Menu By User Category")
    @GetMapping("/menu/list/{userCategory}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuDetails>> getAllMenuByUserCategory(@PathVariable("userCategory") Integer userCategory) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.findAllMenuByUserCategory(userCategory)
        );
    }

    @ApiOperation("List Of Menu By User Login")
    @GetMapping("/menu/permissionAccess")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuTitle>> getAllMenuByUserCategory(Principal principal) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getAllMenuPermissionByUserCategory(principal.getName())
        );
    }

    @ApiOperation("List Of Menu By User Login")
    @GetMapping("/menu/getMenuPermission")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuTitle>> getMenuPermission(Principal principal) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getPermissionMenuAccess(principal.getName())
        );
    }

    @ApiOperation("List of Menu By User Category")
    @GetMapping("/menu/listTitle/{userCategory}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuTitle>> getAllTitleMenuByUserCategory(@PathVariable("userCategory") Integer userCategory) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getMenuByUserCategory(userCategory)
        );
    }
    @ApiOperation("List of Menu By User Category")
    @GetMapping("/menu/listTitleGetAll/{userCategory}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuTitle>> getAllTitle(@PathVariable("userCategory") Integer userCategory) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getAllMenuTitleWithPermission(userCategory)
        );
    }

    @ApiOperation(value = "Save/Update previlage")
    @PostMapping("/menu/save")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> savePrevilage(@RequestBody MenuSettingRequest request) {
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.saveMenuAccess(request));
    }

    @ApiOperation(value = "All Master Menu to show all data")
    @GetMapping("/menu/allMasterMenu")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MenuTitle>> getAllTitle() {
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.getAllMenuPermission());
    }

    @ApiOperation(value = "master title edit data or create")
    @PostMapping("/menu/masterTitle")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> editTileData(@RequestBody MenuTitle request, Principal principal) {
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.createOrEditTitle(request, principal.getName()));
    }

    @ApiOperation(value = "master Parent edit data or create")
    @PostMapping("/menu/masterParent")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> editParentMenuData(@RequestBody MenuDetails request, Principal principal) {
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.createOrEditMenuParent(request, principal.getName()));
    }

    @ApiOperation(value = "master Detail edit data or create")
    @PostMapping("/menu/masterDetail")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> editParentMenuData(@RequestBody MenuList request, Principal principal) {
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                menuService.createOrEditListMenu(request, principal.getName()));
    }
    
    @ApiOperation(value="Set Order Number Menu Title")
    @PostMapping("/menu/masterTitle/orderNumber")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> setOrderNumberTitle(@RequestBody MenuSettingRequest request,Principal principal){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			menuService.saveOrderNumberTitle(request,principal.getName())
    			);
    }
    
    @ApiOperation(value="Set Order Number Menu Parent")
    @PostMapping("/menu/masterParent/orderNumber")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> setOrderNumberParent(@RequestBody MenuSettingRequest request, Principal principal){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			menuService.saveOrderNumberParent(request, principal.getName())
    			);
    }
    
    @ApiOperation(value="Set Order Number Menu Child")
    @PostMapping("/menu/masterDetail/orderNumber")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> setOrderNumberMenu(@RequestBody MenuSettingRequest request,Principal principal){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			menuService.saveOrderNumberMenu(request, principal.getName())
    			);
    }

}
