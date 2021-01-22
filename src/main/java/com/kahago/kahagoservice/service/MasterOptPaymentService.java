package com.kahago.kahagoservice.service;

import static com.kahago.kahagoservice.util.ImageConstant.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.TOptionPaymentEntity;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.ImageRequest;
import com.kahago.kahagoservice.model.request.OptionPaymentRequest;
import com.kahago.kahagoservice.model.request.UserCategory;
import com.kahago.kahagoservice.model.response.OptionPaymentResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MOptionPaymentRepo;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author Ibnu Wasis
 */
@Service
public class MasterOptPaymentService {
	@Autowired
	private MOptionPaymentRepo mOptionPaymentRepo;
	@Autowired
	private TOptionPaymentRepo tOptionPaymentRepo;
	@Autowired
	private MUserCategoryRepo mUserCategoryRepo;
	
	@Value("${kahago.image.optionPayment}")
	private String uploadingDir;
	
	public List<OptionPaymentResponse> getAllOptionPayment(){
		List<MOptionPaymentEntity> loptPay = mOptionPaymentRepo.findAll();
		return loptPay.stream().map(this::toDtoOptPayment).collect(Collectors.toList());
	}
	
	public OptionPaymentResponse toDtoOptPayment(MOptionPaymentEntity entity) {
		String images = PREFIX_PATH_IMAGE_PAYMENT_OPTION+ entity.getPathImage()
    			.substring(entity.getPathImage().lastIndexOf("/") + 1);
		return OptionPaymentResponse.builder()
				.seqid(entity.getSeqid())
				.codePayment(entity.getCode())
				.codeVendor(entity.getCodeVendor().toString())
				.description(entity.getDescription())
				.isActive(entity.getIsActive())
				.isPhone(entity.getIsPhone())
				.operator(entity.getOperatorSw())
				.isPayment(entity.getIsPayment())
				.isDeposit(entity.getIsDeposit())
				.minNominal(entity.getMinNominal().intValue())
				.offTimeStart(entity.getOffTimeStart().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
				.offTimeEnd(entity.getOffTimeEnd().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
				.images(images)
				.build();
	}
	@Transactional
	public SaveResponse saveOptionPayment(OptionPaymentRequest request) {
		MOptionPaymentEntity entity = new MOptionPaymentEntity();
		if(request.getOffTimeStart()==null) {
			request.setOffTimeStart("00:00:00");
		}
		if(request.getOffTimeEnd()==null) {
			request.setOffTimeEnd("00:00:00");
		}
		entity.setCode(request.getCode());
		entity.setDescription(request.getDescription());
		entity.setCodeVendor(request.getCodeVendor());
		entity.setIsActive(request.getIsActive());
		entity.setPathImage(uploadFile(request.getImage()));
		entity.setIsDeposit(request.getIsDeposit());
		entity.setIsPayment(request.getIsPayment());
		entity.setIsPhone(request.getIsPhone());
		entity.setOperatorSw(request.getOperator());
		entity.setMinNominal(new BigDecimal(request.getMinNominal()));
		entity.setOffTimeEnd(LocalTime.parse(request.getOffTimeEnd(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		entity.setOffTimeStart(LocalTime.parse(request.getOffTimeStart(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		entity = mOptionPaymentRepo.save(entity);
		for(UserCategory us : request.getUserCategory()) {
			TOptionPaymentEntity topt = new TOptionPaymentEntity();
			MUserCategoryEntity ucat = mUserCategoryRepo.findBySeqid(us.getUserCategoryId());
			topt.setCode(entity.getCode());
			topt.setIsDeposit(entity.getIsDeposit());
			topt.setIsPayment(entity.getIsPayment());
			topt.setOptionPayment(entity);
			topt.setUserCategory(ucat);
			tOptionPaymentRepo.save(topt);
		}
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Cara Pembayaran")
				.build();
	}
	@Transactional
	public SaveResponse saveEdit(OptionPaymentRequest  request) {
		MOptionPaymentEntity entity = mOptionPaymentRepo.findBySeqid(request.getId());
		if(entity == null) {
			throw new NotFoundException("Data Tidak Ditemukan !");
		}
		entity.setCode(request.getCode());
		entity.setDescription(request.getDescription());
		entity.setCodeVendor(request.getCodeVendor());
		entity.setIsActive(request.getIsActive());
		entity.setPathImage(uploadFile(request.getImage()));
		entity.setIsDeposit(request.getIsDeposit());
		entity.setIsPayment(request.getIsPayment());
		entity.setIsPhone(request.getIsPhone());
		entity.setMinNominal(new BigDecimal(request.getMinNominal()));
		entity.setOffTimeEnd(LocalTime.parse(request.getOffTimeEnd(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		entity.setOffTimeStart(LocalTime.parse(request.getOffTimeStart(), DateTimeFormatter.ofPattern("HH:mm:ss")));
		entity = mOptionPaymentRepo.save(entity);
		tOptionPaymentRepo.deleteByOptionPayment(entity.getSeqid());
		for(UserCategory us : request.getUserCategory()) {
			TOptionPaymentEntity topt = new TOptionPaymentEntity();
			MUserCategoryEntity ucat = mUserCategoryRepo.findBySeqid(us.getUserCategoryId());
			topt.setCode(entity.getCode());
			topt.setIsDeposit(entity.getIsDeposit());
			topt.setIsPayment(entity.getIsPayment());
			topt.setOptionPayment(entity);
			topt.setUserCategory(ucat);
			tOptionPaymentRepo.save(topt);
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Edit Cara Pembayaran")
				.build();
	}
	@Transactional
	public SaveResponse deleteMOptionPayment(Integer id) {
		MOptionPaymentEntity entity = mOptionPaymentRepo.findBySeqid(id);
		if(entity.getIsActive()) {
			entity.setIsActive(false);
		}else {
			entity.setIsActive(true);
		}
		entity = mOptionPaymentRepo.save(entity);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Aktif/NonAktif Cara Pembayaran")
				.build();
	}
	private String uploadFile(ImageRequest req) {
		String path="";
		try {
			byte[] bytes = Base64.decodeBase64(req.getContent());
			path=uploadingDir+req.getFileName().substring(0,req.getFileName().lastIndexOf("."))+".svg";
			Path fileLoc  = Paths.get(path);
			Files.write(fileLoc, bytes);
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		}
		return path;
	}
}
