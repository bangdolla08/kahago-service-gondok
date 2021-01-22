package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.request.ProductSwitcherRequest;
import com.kahago.kahagoservice.model.response.ProductSwitcherResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.ProductSwitcherService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Slf4j
public class ProductSwitcherController extends Controller{
	@Autowired
	private ProductSwitcherService productSwitcherService;
	
	@GetMapping("productsw/list")
	@ApiOperation("List of Product Switcher")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ProductSwitcherResponse>> getList(@RequestParam(name="cari",required=false,defaultValue="")String cari,
    														PageHeaderRequest pageable){
		log.info("==> List Of Product <==");
		Page<ProductSwitcherResponse> lProdcut = productSwitcherService.getListProduct(cari, pageable.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(lProdcut),
				lProdcut.getContent()
				);
	}
	
	@GetMapping("productsw/get/{id}")
	@ApiOperation("get By Product Switcher Code")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<ProductSwitcherResponse> getById(@PathVariable("id") Integer id){
		log.info("==> Get Product By Id <==");
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				productSwitcherService.getByProductSwCode(id)
				);
	}
	
	@PostMapping("productsw/saveAdd")
	@ApiOperation("Save new Product Switcher")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveAdd(@RequestBody ProductSwitcherRequest request,Principal principal){
		log.info("==> Add Product <==");
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				productSwitcherService.saveProductSwitcher(request,principal.getName())
				);
	}
	
	@PostMapping("productsw/saveEdit")
	@ApiOperation("Save Edit Product Switcher")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveEdit(@RequestBody ProductSwitcherRequest request,Principal principal){
		log.info("==> Edit Product <==");
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				productSwitcherService.saveEdit(request,principal.getName())
				);
	}
	
	@DeleteMapping("productsw/delete/{id}")
	@ApiOperation("Non Activated Product Switcher")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> deleteProductSw(@PathVariable("id")Integer id){
		log.info("==> Delete Product <==");
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				productSwitcherService.nonActiveProduct(id)
				);
	}
}
