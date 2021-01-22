package com.kahago.kahagoservice.client;

import com.kahago.kahagoservice.client.model.request.ReqTrackingLP;
import com.kahago.kahagoservice.client.model.response.ResTracking;
import com.kahago.kahagoservice.client.model.response.ResTrackingLP;
import com.kahago.kahagoservice.model.response.AreaResponse;
import com.kahago.kahagoservice.model.response.BookResponseBukaSend;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

/**
 * @author Hendro yuwono
 */
@FeignClient(url = "http://example-default-gondok.com", name = "FEIGN-SERVICE")
public interface FeignService {

    @GetMapping(consumes = "application/json")
    ResTracking fetchTrackingByResi(URI uri, @RequestParam(name = "noresi") String noresi);

    @PostMapping(consumes = "application/json")
    ResTrackingLP fetchTrackByResiLionParcel(URI uri, ReqTrackingLP body);
    
    @GetMapping(consumes ="application/json")
    ResTracking fetchTrackingByResiPCP(URI uri);
    
    @GetMapping(consumes= "application/json")
    AreaResponse fetchPriceArea(URI uri);
    
    @GetMapping(consumes="application/json")
    String fetchManualCronTracking(URI uri, @RequestParam(required=false,name= "bookingCode") String bookingCode,@RequestParam(required=false,name="vendorCode")Integer vendorCode);
    
    @GetMapping(consumes="application/json")
    BookResponseBukaSend fetchGetDetailBukaSend(URI uri);
}
