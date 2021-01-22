package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.MPermohonanEntity;
import com.kahago.kahagoservice.entity.THppPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPermohonanEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PermohonanDetailEnum;
import com.kahago.kahagoservice.enummodel.PermohonanEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.DetailSave;
import com.kahago.kahagoservice.model.request.PermohonanListReq;
import com.kahago.kahagoservice.model.request.PermohonanReq;
import com.kahago.kahagoservice.model.request.PermohonanSaveReq;
import com.kahago.kahagoservice.model.request.ReqBook;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.DetailPermohonan;
import com.kahago.kahagoservice.model.response.PermohonanDetailResp;
import com.kahago.kahagoservice.model.response.PermohononanListRespon;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MPermohonanRepo;
import com.kahago.kahagoservice.repository.THppPaymentRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPermohonanRepo;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 8 Jun 2020
 */
@Service
public class PermohonanService {
	@Autowired
	private TPaymentRepo payrepo;
	@Autowired
	private TPermohonanRepo tPermohonanRepo;
	@Autowired
	private MPermohonanRepo mPermohonanRepo;
	@Autowired
	private THppPaymentRepo hppRepo;
	
	public Page<PermohononanListRespon> getAllListPermohonan(PermohonanListReq req){
		Page<MPermohonanEntity> lsPermohonan = tPermohonanRepo.findAllByStatusAndVendorAndPermohonanAndBookId(req.getStatus(), req.getVendorCode(), req.getNoPermohonan(),req.getBookId(),req.getPageRequest());
		
		return new PageImpl<>(
				lsPermohonan.getContent().stream().map(this::listMohon).collect(Collectors.toList()), 
				lsPermohonan.getPageable(), 
				lsPermohonan.getTotalElements());
	}
	
	private PermohononanListRespon listMohon(MPermohonanEntity pv) {
		
		return PermohononanListRespon.builder()
				.noPermohonan(pv.getNomorPermohonan())
				.status(pv.getStatus())
				.trxDate(pv.getLastUpdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
				.createDate(pv.getCreateDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
				.createUser(pv.getCreateUser())
				.lastUser(pv.getLastUser())
				.build();
	}
	
	public PermohononanListRespon getDetailPermohonan(String noPV,String userid){
		List<Integer> lsstatus = Arrays.asList(PermohonanDetailEnum.DRAFT.getValue(),
				PermohonanDetailEnum.VERIFIED.getValue(),
				PermohonanDetailEnum.WAITING_APPROVE.getValue(),
				PermohonanDetailEnum.REJECT.getValue());
		MPermohonanEntity pv = mPermohonanRepo.findById(noPV).orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nomor Permohonan Tidak Ditemukan"));
		List<TPermohonanEntity> lspv = tPermohonanRepo.findByNomorPermohonanAndStatusIn(pv,lsstatus);
		List<PermohonanDetailResp> lsDetail = lspv.stream().map(this::listDetailPermohonan).collect(Collectors.toList());
		PermohononanListRespon resp = new PermohononanListRespon();
		resp = listMohon(pv);
		resp.setDetails(lsDetail);
		return resp;
	}
	
	private PermohonanDetailResp listDetailPermohonan(TPermohonanEntity permohonan) {
		THppPaymentEntity hpp = hppRepo.findByBookingCode(permohonan.getBookingCode()).orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hpp Tidak ditemukan"));
		List<TPermohonanEntity> lsold = tPermohonanRepo.findByBookingCodeAndNoInvoice(permohonan.getBookingCode(),permohonan.getNoInv());
		List<DetailPermohonan> ldDetail = lsold.stream().map(this::getDetailOld).collect(Collectors.toList());
		PermohonanDetailResp pvd = PermohonanDetailResp.builder()
				.idPermohonan(permohonan.getSeqid())
				.bookId(permohonan.getBookingCode().getBookingCode())
				.invoiceVendor(permohonan.getNoInv())
				.priceVendor(permohonan.getAmountVendor().toString())
				.product(permohonan.getBookingCode().getProductSwCode().getDisplayName())
				.stt(permohonan.getBookingCode().getStt())
				.totalHpp(hpp.getHpp().toString())
				.totalHppActual(hpp.getHppActual().toString())
				.totalWeight(permohonan.getBookingCode().getGrossWeight().toString())
				.totalPrice(permohonan.getBookingCode().getPrice().toString())
				.trxDate(permohonan.getNomorPermohonan().getLastUpdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
				.vendor(permohonan.getBookingCode().getProductSwCode().getSwitcherEntity().getDisplayName())
				.destination(permohonan.getBookingCode().getDestination())
				.status(permohonan.getStatus())
				.oldPayment(ldDetail)
				.build();
		return pvd;
	}
	
	private DetailPermohonan getDetailOld(TPermohonanEntity pv) {
		return DetailPermohonan.builder()
				.bookId(pv.getBookingCode().getBookingCode())
				.invVendor(pv.getNoInv())
				.priceVendor(pv.getAmountVendor().toString())
				.noPermohonan(pv.getNomorPermohonan().getNomorPermohonan())
				.statusPermohonan(pv.getNomorPermohonan().getStatus().toString())
				.build();
	}
	
	public List<PermohonanDetailResp> getDataBook(PermohonanReq req){
//		MPermohonanEntity pemohon = mPermohonanRepo.findById(req.getNoPermohonan()).get();
		List<Integer> lsStatus = Arrays.asList(PaymentEnum.SEND_TO_VENDOR.getCode(),PaymentEnum.PICK_BY_VENDOR.getCode(),PaymentEnum.RECEIVE.getCode());
		List<TPaymentEntity> lspay = payrepo.findByBookingCodeOrStt(req.getBooks().stream().map(p->p.getBookId()).collect(Collectors.toList()), lsStatus);
		if(lspay.isEmpty()) throw new NotFoundException("Kode Book Atau STT Tidak Ditemukan");
		List<PermohonanDetailResp> lsDetail = lspay.stream().map(d-> getDetailsDataBook(d,req.getBooks().stream().filter(p-> p.getBookId().equalsIgnoreCase(d.getBookingCode()) || p.getBookId().equalsIgnoreCase(d.getStt())).findAny().get())).collect(Collectors.toList());
		return lsDetail;
	}
	
	public PermohonanDetailResp getDetailDataBook(TPaymentEntity pay,MPermohonanEntity permohonan) {
		List<Integer> lsStatusPay = Arrays.asList(PaymentEnum.SEND_TO_VENDOR.getCode(),PaymentEnum.PICK_BY_VENDOR.getCode());
		THppPaymentEntity hpp = hppRepo.findByBookingCode(pay).orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hpp Tidak ditemukan"));
		List<TPermohonanEntity> lsold = tPermohonanRepo.findAllByBookingCodeAndBookingCodeStatusIn(pay,lsStatusPay);
		List<DetailPermohonan> ldDetail = lsold.stream().map(this::getDetailOld).collect(Collectors.toList());
		PermohonanDetailResp pvd = PermohonanDetailResp.builder()
				.noPermohonan(permohonan.getNomorPermohonan())
				.bookId(pay.getBookingCode())
				.invoiceVendor("")
				.priceVendor("0")
				.product(pay.getProductSwCode().getDisplayName())
				.stt(pay.getStt())
				.totalHpp(hpp.getHpp().toString())
				.totalHppActual(hpp.getHppActual().toString())
				.totalWeight(pay.getGrossWeight().toString())
				.totalPrice(pay.getPrice().toString())
				.trxDate(permohonan.getLastUpdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
				.vendor(pay.getProductSwCode().getSwitcherEntity().getDisplayName())
				.status(0)
				.oldPayment(ldDetail)
				.build();
		return pvd;
	}
	
	public PermohonanDetailResp getDetailsDataBook(TPaymentEntity pay,ReqBook books) {
//		ReqBook book = books.stream().filter(p-> p.getBookId().equalsIgnoreCase(pay.getBookingCode()) || p.getBookId().equalsIgnoreCase(pay.getStt())).findAny().get();
		List<Integer> lsStatusPay = Arrays.asList(PaymentEnum.SEND_TO_VENDOR.getCode(),PaymentEnum.PICK_BY_VENDOR.getCode());
		THppPaymentEntity hpp = hppRepo.findByBookingCode(pay).orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hpp Tidak ditemukan"));
		List<TPermohonanEntity> lsold = tPermohonanRepo.findAllByBookingCodeAndBookingCodeStatusIn(pay,lsStatusPay);
		List<DetailPermohonan> ldDetail = lsold.stream().map(this::getDetailOld).collect(Collectors.toList());
		PermohonanDetailResp pvd = PermohonanDetailResp.builder()
				.bookId(pay.getBookingCode())
				.seqid(books.getSeqid())
				.invoiceVendor("")
				.priceVendor("0")
				.product(pay.getProductSwCode().getDisplayName())
				.stt(pay.getStt())
				.totalHpp(hpp.getHpp().toString())
				.totalHppActual(hpp.getHppActual().toString())
				.totalWeight(pay.getGrossWeight().toString())
				.totalPrice(pay.getPrice().toString())
				.trxDate(pay.getTrxDate().toString())
				.vendor(pay.getProductSwCode().getSwitcherEntity().getDisplayName())
				.destination(pay.getDestination())
				.status(0)
				.oldPayment(ldDetail)
				.build();
		return pvd;
	}
	
	public PermohonanDetailResp getDetailDataBookKebijakan(TPermohonanEntity mohon) {
		THppPaymentEntity hpp = hppRepo.findByBookingCode(mohon.getBookingCode()).orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Hpp Tidak ditemukan"));
		PermohonanDetailResp pvd = PermohonanDetailResp.builder()
				.noPermohonan(mohon.getNomorPermohonan().getNomorPermohonan())
				.bookId(mohon.getBookingCode().getBookingCode())
				.invoiceVendor(mohon.getNoInv())
				.priceVendor(mohon.getAmountVendor().toString())
				.product(mohon.getBookingCode().getProductSwCode().getDisplayName())
				.stt(mohon.getBookingCode().getStt())
				.totalHpp(hpp.getHpp().toString())
				.totalHppActual(mohon.getHppActual().toString())
				.totalWeight(mohon.getBookingCode().getGrossWeight().toString())
				.totalPrice(mohon.getBookingCode().getPrice().toString())
				.trxDate(mohon.getLastUpdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
				.vendor(mohon.getBookingCode().getProductSwCode().getSwitcherEntity().getDisplayName())
				.destination(mohon.getBookingCode().getDestination())
				.status(mohon.getStatus())
				.build();
		return pvd;
	}
	
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, rollbackFor = Exception.class)
	public SaveResponse doSavePermohonan(PermohonanSaveReq req,String userid) {
		MPermohonanEntity pv = mPermohonanRepo.findByNomorPermohonan(req.getNoPermohonan()).orElse(MPermohonanEntity.builder()
					.createDate(LocalDateTime.now())
					.createUser(userid)
					.lastUpdate(LocalDateTime.now())
					.lastUser(userid)
					.nomorPermohonan(getCounter())
					.status(PermohonanEnum.DRAFT.getValue())
					.build());
		if(req.getNoPermohonan()==null) {
			mPermohonanRepo.saveAndFlush(pv);
		}
		
		PermohonanSaveReq resp = new PermohonanSaveReq();
		List<DetailSave> details = req.getDatas().stream().map(d-> getDetailSave(d, pv)).collect(Collectors.toList());
		details = details.stream().filter(d->d!=null).collect(Collectors.toList());
		resp.setDatas(details);
		resp.setNoPermohonan(pv.getNomorPermohonan());
		Long countHold = tPermohonanRepo.countByNomorPermohonanAndStatus(pv, PermohonanDetailEnum.WAITING_APPROVE.getValue());//details.stream().filter(pe->pe.getStatus().equals("2")).count();
//		details = details.stream().filter(p-> p.getStatus().equals("1")).collect(Collectors.toList());
		 if(countHold>0) {
			pv.setStatus(PermohonanEnum.WAITING_APPROVE.getValue());
		 }else {
			pv.setStatus(PermohonanEnum.DRAFT.getValue());
		 }
		 mPermohonanRepo.saveAndFlush(pv);
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.build();
	}
	
	
	private DetailSave getDetailSave(DetailSave detail,MPermohonanEntity pv) {
		DetailSave detailSave = detail;
		detailSave.setDescription(PermohonanDetailEnum.getEnumByNumber(Integer.valueOf(detail.getStatus())).name());
		Integer state = saveDetailPermohonan(detail, pv);
		switch (state) {
		case 1:
			detailSave.setStatus("1");
			detailSave.setDescription("Verified");
			break;
		case 2:
			detailSave.setStatus("2");
			detailSave.setDescription(PermohonanDetailEnum.WAITING_APPROVE.name());
			break;
		case -1:
			detailSave=null;
			break;
		default:
			detailSave.setStatus("0");
			detailSave.setDescription("Duplicate");
			break;
		}
		return detailSave;
	}
	private Integer saveDetailPermohonan(DetailSave detail, MPermohonanEntity permohonan) {
		TPaymentEntity pay = payrepo.findByBookingCodeIgnoreCaseContaining(detail.getBookId());
		if(detail.getIsNew()) detail.setIdPermohonan("0");
		TPermohonanEntity pv = tPermohonanRepo.findBySeqidAndBookingCodeAndNoInv(Integer.parseInt(detail.getIdPermohonan()),pay, detail.getInvoiceVendor())
				.orElse(tPermohonanRepo.findByBookingCodeAndNoInv(pay, detail.getInvoiceVendor()));
		THppPaymentEntity hpp = hppRepo.findByBookingCode(pay).get();
		BigDecimal hppActual = this.getHppActual(hpp, new BigDecimal(detail.getPriceVendor()));
		Integer status = 0;
		if(pv==null) {
			if(PermohonanDetailEnum.DELETE==PermohonanDetailEnum.getEnumByNumber(Integer.valueOf(detail.getStatus()))){
				return -1;
			}
			status = (checkHpp(hpp, hppActual))?1:2;
			TPermohonanEntity tp = TPermohonanEntity.builder()
					.amountVendor(new BigDecimal(detail.getPriceVendor()))
					.bookingCode(pay)
					.noInv(detail.getInvoiceVendor())
					.nomorPermohonan(permohonan)
					.hppActual(hppActual)
					.status(status)
					.lastUpdate(LocalDateTime.now())
					.lastUser(permohonan.getLastUser())
					.build();
			tPermohonanRepo.saveAndFlush(tp);
			if(status==1)
				this.doSaveHPP(hpp, hppActual, status);
			return status;
		}else {
			if(PermohonanDetailEnum.REJECT==PermohonanDetailEnum.getEnumByNumber(Integer.valueOf(detail.getStatus())))
				return -1;
			updateHpp(pv.getAmountVendor(), pv.getStatus(), hpp,"-1");
			if(-1==Integer.valueOf(detail.getStatus()) 
					&& PermohonanDetailEnum.WAITING_APPROVE==PermohonanDetailEnum.getEnumByNumber(pv.getStatus())
					) {
				pv.setStatus(PermohonanDetailEnum.DELETE.getValue());
				tPermohonanRepo.saveAndFlush(pv);
				return -1;
			}else 
			{
				hppActual = this.getHppActual(hpp, new BigDecimal(detail.getPriceVendor()));
				status = (checkHpp(hpp, hppActual))?1:2;
				pv.setAmountVendor(new BigDecimal(detail.getPriceVendor()));
				pv.setStatus(Integer.parseInt(detail.getStatus()));
				if(1==status) {
					pv.setHppActual(updateHpp(new BigDecimal(detail.getPriceVendor()), status, hpp,"1"));
				}
				tPermohonanRepo.saveAndFlush(pv);
				return status;
			}
		}
		
	}

	private BigDecimal updateHpp(BigDecimal PriceVendor, Integer status, THppPaymentEntity hpp,String kali) {
		BigDecimal hppActual = this.getHppActual(hpp, PriceVendor.multiply(new BigDecimal(kali)));
		this.doSaveHPP(hpp, hppActual, status);
		hppActual = (hppActual.doubleValue()<0)?PriceVendor:hppActual;
		return hppActual;
	}
	
	private String getCounter() {
    	String counter = mPermohonanRepo.findLastNomor();
    	if(counter==null) {
    		counter = "00000";
    	}
    	int count = Integer.valueOf(counter) + 1;
    	String format = counter.substring(0, counter.length()-String.valueOf(count).length()) + String.valueOf(count);
    	return "PV" + format;
    }
	
	public List<PermohonanDetailResp> doSaveApprovalReject(PermohonanSaveReq req,String userid) {
		List<TPermohonanEntity> lsPermohonan = req.getDatas().stream().map(d-> getDataApprovalReject(d, userid,req.getNoPermohonan())).collect(Collectors.toList());
		if(lsPermohonan.isEmpty()==false) {
			MPermohonanEntity permohonanEntity = lsPermohonan.stream().findAny().get().getNomorPermohonan();
			permohonanEntity.setStatus(PermohonanEnum.DRAFT.getValue());
			permohonanEntity.setLastUpdate(LocalDateTime.now());
			permohonanEntity.setLastUser(userid);
			mPermohonanRepo.saveAndFlush(permohonanEntity);
		}
		tPermohonanRepo.saveAll(lsPermohonan);
		List<PermohonanDetailResp> lsDetailResp = lsPermohonan.stream()
				.map(p-> getDetailDataBook(p.getBookingCode(), p.getNomorPermohonan())).collect(Collectors.toList());
		return lsDetailResp;
	}

	public TPermohonanEntity getDataApprovalReject(DetailSave detail,String userid,String nopermohonan) {
		TPermohonanEntity permohonan = tPermohonanRepo.findByApprovalReject(nopermohonan,detail.getBookId(),detail.getInvoiceVendor()).orElseThrow(()-> new InternalServerException("Data Tidak Ditemukan"));
		if(detail.getStatus().equals("1")) {
			Log.info("Approve Book "+permohonan.getBookingCode().getBookingCode()+" - "+permohonan.getNomorPermohonan().getNomorPermohonan());
			THppPaymentEntity hpp = hppRepo.findByBookingCode(permohonan.getBookingCode()).get();
			this.doSaveHPP(hpp, permohonan.getHppActual(), PermohonanDetailEnum.VERIFIED.getValue());
		}
		permohonan.setStatus(PermohonanDetailEnum.getEnumByNumber(Integer.valueOf(detail.getStatus())).getValue());
		permohonan.setReason(detail.getDescription());
		permohonan.setLastUpdate(LocalDateTime.now());
		permohonan.setLastUser(userid);
		return permohonan;
	}
	
	private BigDecimal getHppActual(THppPaymentEntity hpp,BigDecimal priceVendor) {
		BigDecimal hppActual = hpp.getHppActual().add(priceVendor);
		
		return hppActual;
	}
	
	private void doSaveHPP(THppPaymentEntity hpp,BigDecimal hppActual, Integer status) {
		if(PermohonanDetailEnum.getEnumByNumber(status)!=PermohonanDetailEnum.WAITING_APPROVE) {
			hpp.setHppActual(hppActual);
			hppRepo.saveAndFlush(hpp);
		}
	}
	
	private Boolean checkHpp(THppPaymentEntity hpp,BigDecimal hppActual) {
		return (hpp.getHpp().doubleValue()>=hppActual.doubleValue())?true:false;
	}
	
	public SaveResponse doPrinting(String noPermohonan,String userid) {
		Log.info("Printing nomor Permohonan ==> "+noPermohonan);
		MPermohonanEntity permohonan = mPermohonanRepo
				.findByNomorPermohonanAndStatusNotIn(noPermohonan,PermohonanEnum.WAITING_APPROVE.getValue()).orElseThrow(()-> new InternalServerException("Data Tidak Ditemukan atau sudah terproses"));
		permohonan.setLastUpdate(LocalDateTime.now());
		permohonan.setLastUser(userid);
		permohonan.setStatus(PermohonanEnum.PROPOSED.getValue());
		mPermohonanRepo.save(permohonan);
		List<TPermohonanEntity> lsPermohonan = tPermohonanRepo.findByNomorPermohonanAndStatusIn(permohonan,Arrays.asList(PermohonanDetailEnum.DRAFT.getValue(),
				PermohonanDetailEnum.VERIFIED.getValue()));
		lsPermohonan.forEach(p->p.setStatus(PermohonanDetailEnum.VERIFIED.getValue()));
		tPermohonanRepo.saveAll(lsPermohonan);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.linkResi("api/report/permohonan?nopermohonan="+noPermohonan)
				.build();
	}
	
	public SaveResponse doDeletePermohonan(Integer idPermohonan,String userid) {
		Log.info("Printing id Permohonan ==> "+idPermohonan);
		TPermohonanEntity permohonanEntity = tPermohonanRepo.findById(idPermohonan).orElseThrow(()-> new InternalServerException("Data Tidak Ditemukan atau sudah terproses"));
		permohonanEntity.setStatus(PermohonanDetailEnum.DELETE.getValue());
		permohonanEntity.setLastUser(userid);
		permohonanEntity.setLastUpdate(LocalDateTime.now());
		tPermohonanRepo.save(permohonanEntity);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.build();
	}
}
