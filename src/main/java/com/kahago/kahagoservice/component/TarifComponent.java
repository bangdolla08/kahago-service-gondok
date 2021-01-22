package com.kahago.kahagoservice.component;

import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TarifComponent {
	@Async("asyncExecutor")
	@SneakyThrows
	public void getDirectPrice(String fromCode,String toCode) {
		String path = "http://206.189.94.139:8180/mappingpricemanualkec/{fromCode}/{toCode}";
		path = path.replace("{fromCode}", fromCode)
				.replace("{toCode}", toCode);
		RestTemplate rest = new RestTemplate();
		HttpEntity<String> response = rest.getForEntity(path, String.class);
		log.info("Response=> "+response.getBody().toString());
//		return false;
	}
}
