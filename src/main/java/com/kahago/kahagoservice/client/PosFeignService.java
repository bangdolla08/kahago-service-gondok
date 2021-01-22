package com.kahago.kahagoservice.client;

import com.kahago.kahagoservice.client.model.response.RespOpenLayanan;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

/**
 * @author Hendro yuwono
 */
@FeignClient(url = "${url.service.pos}", name = "POS-FEIGN-SERVICE")
public interface PosFeignService {

    @GetMapping("/sheet/open")
    RespOpenLayanan openLayananPos();

    @GetMapping
    RespOpenLayanan createManifestPos(URI uri,
                                      @RequestParam(value = "resi") String counting,
                                      @RequestParam(value = "userid") String clientCode,
                                      @RequestParam(value = "parentpos") String parentPos,
                                      @RequestParam(value = "agenid") String agentId);

    @GetMapping
    RespOpenLayanan closeManifestPos(URI uri,
                                      @RequestParam(value = "manifest") String manifestNumber,
                                      @RequestParam(value = "userid") String userId,
                                      @RequestParam(value = "parentpos") String parentPos,
                                      @RequestParam(value = "agenid") String agentId);
}
