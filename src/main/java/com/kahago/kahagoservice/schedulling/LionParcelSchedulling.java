package com.kahago.kahagoservice.schedulling;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.component.vendor.LionParcelComponent;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MVendorPropEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TSttVendorEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.BookRequestLP;
import com.kahago.kahagoservice.model.response.BookResponseLP;
import com.kahago.kahagoservice.repository.MVendorPropRepo;
import com.kahago.kahagoservice.repository.THistoryBookRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TSttVendorRepo;
import com.kahago.kahagoservice.service.WarehouseVerificationService;
import com.kahago.kahagoservice.util.Common;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LionParcelSchedulling {
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private LionParcelComponent lpcomp;
	@Autowired
	private MVendorPropRepo vendorPropRepo;
	@Autowired
	private TSttVendorRepo tSttVendorRepo;
	@Scheduled(cron="${cron.lp.auto-approve}")
	@SneakyThrows
	public void doApproveLP() {
		log.info("==> Lion Parcel Approval");
		List<TPaymentEntity> lsPay = payRepo.findByStatusIn(Arrays.asList(PaymentEnum.ACCEPT_WITHOUT_RESI.getCode()));
		MVendorPropEntity vendorProp = null;
		for(TPaymentEntity pay:lsPay) {
			BookResponseLP resp = new BookResponseLP();
			MSwitcherEntity switcherCode = pay.getProductSwCode().getSwitcherEntity();
			if(pay.getStatus().equals("-") || pay.getStt().trim().isEmpty()) {
				TSttVendorEntity stt = tSttVendorRepo.findFirstBySwitcherCodeAndFlagAndOrigin(switcherCode.getSwitcherCode(), 0, null);
				pay.setStt(stt.getStt());
			}
			vendorProp = vendorPropRepo.findAllBySwitcherCodeAndActionAndOrigin(switcherCode, "book", pay.getOrigin());
			resp =  lpcomp.getPayment(vendorProp.getClientCode(), pay.getStt(), pay.getProductSwCode().getOperatorSw(), vendorProp.getUrl(), pay);
			pay.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
			log.info("Respon From LP:"+Common.json2String(resp));
			if(!resp.getRc().equals(ResponseStatus.OK.value())) {
				pay.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
			}
		}
		
		payRepo.saveAll(lsPay);
		
	}
}
