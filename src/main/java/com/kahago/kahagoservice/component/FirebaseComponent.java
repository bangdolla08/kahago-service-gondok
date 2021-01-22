package com.kahago.kahagoservice.component;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.SneakyThrows;

/**
 * @author Ibnu Wasis
 */
@Component
public class FirebaseComponent {
	private static final Logger logger = LoggerFactory.getLogger(FirebaseComponent.class);
	private static String URL_NOTIF="http://localhost:8099/send";
	private static String URL_NOTIF_TOPICS="http://localhost:8099/sendtopics";
	
	@Async("asyncExecutor")
	public void notif(String title,String desc, JSONObject data,String tag,String token) {
		RestTemplate rest = new RestTemplate();
		JSONObject body = new JSONObject();
		try {
			body.put("to", token);
			body.put("notification",  getNotification(title, desc));
			body.put("apns", getAPN());
			body.put("priority", "high");
			data.put("date", DateTimeUtil.getDateTime("dd MMM yyyy kk:mm"));
			body.put("data", data);
			HttpEntity<String> request = new HttpEntity<>(body.toString());
			ResponseEntity<String> response = null;
			response = rest.postForEntity(URL_NOTIF, request, String.class);
			logger.info("Response:-> " + response.getBody());
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	private JSONObject getNotification(String title, String desc) throws JSONException {
		JSONObject notif = new JSONObject();
		notif.put("body", desc);
		notif.put("title", title);
		return notif;
	}
	
	@SneakyThrows
	private JSONObject getAPN() {
		JSONObject apn = new JSONObject();
		JSONObject payload = new JSONObject();
		payload.put("aps", new JSONObject().put("mutable-content", 1));
		apn.put("payload", payload);
		
		return apn;
	}
	public void notifAll(String tittle,
			String desc,JSONObject data,String tag,String topik) {
		RestTemplate rest = new RestTemplate();
		JSONObject body = new JSONObject();
		try {
			//dev topik
			topik = "infodev";
			body.put("to", "/topics/"+topik);
			body.put("priority", "high");
			body.put("notification",  getNotification(tittle, desc));
			body.put("apns", getAPN());
			body.put("data", data);
			HttpEntity<String> request = new HttpEntity<>(body.toString());
			ResponseEntity<String> response = null;
			response = rest.postForEntity(URL_NOTIF_TOPICS, request, String.class);
			logger.info("Response:-> " + response.getBody());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
	}
	
	@Async("asyncExecutor")
	public void triggerOrderPay(String token) {
		JSONObject body = new JSONObject();
		JSONObject data = new JSONObject();
		RestTemplate rest = new RestTemplate();
		try {
			body.put("to", token);
			body.put("priority", "high");
			
			data.put("idTrx", "pesanan"); 
	    	data.put("date", DateTimeUtil.getDateTime("dd MMM yyyy kk:mm"));
	    	data.put("userid", "order");
	    	data.put("nominal", "00"); //nominal
	    	data.put("type_trx", "3"); //1. Book, 2. Deposit, 3. Payment
	    	data.put("tag", "orderlist");
	    	data.put("tittle", "Refresh Pemesanan");
	    	data.put("status_trx", "0");
			data.put("body", "[\"test\"]"); //description
			
			body.put("data", data);
			HttpEntity<String> request = new HttpEntity<>(body.toString());
			ResponseEntity<String> response = null;
			response = rest.postForEntity(URL_NOTIF, request, String.class);
			logger.info("Response Trigger:-> " + response.getBody());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

}
