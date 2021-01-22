package com.kahago.kahagoservice.client;

import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.model.response.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 24 Jun 2020
 */
@FeignClient(url = "${url.cron.ip}", name = "CRON-FEIGN-SERVICE")
public interface CronFeignService {
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mapping/update/1")
    SaveResponse updateProv(VendorArea body);
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mapping/update/2")
    SaveResponse updateKota(VendorArea body);
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mapping/update/3")
    SaveResponse updateKecamatan(VendorArea body);
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mapping/update/4")
    SaveResponse updateKelurahan(VendorArea body);
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mapping/update/vendorarea")
    SaveResponse updateVendorArea(VendorAreaDetail body);
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mapping/update/area")
	SaveResponse updateTArea(TArea body);
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mapping/update/productSwitcher")
	SaveResponse updateProductSwitcher(ProductSwitcher body);

	@GetMapping("/urgent/{areadetail}")
	SaveResponse updateUrgentArea(@PathVariable Integer areadetail);
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/mappingpricemanual/vendor/fromcode/{vendor}/{fromCode}/{toCode}")
	ResponseModel updateAreaByIdPostalCodeAndVendor(@PathVariable String vendor,@PathVariable String fromCode,@PathVariable String toCode);
}
