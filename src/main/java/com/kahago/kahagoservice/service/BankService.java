package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MBankEntity;
import com.kahago.kahagoservice.model.response.BankDeposit;
import com.kahago.kahagoservice.model.response.BankRes;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.MBankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.kahago.kahagoservice.util.ImageConstant.*;

/**
 * @author Hendro yuwono
 */
@Controller
public class BankService {

    @Autowired
    private MBankRepo bankRepo;

    @Autowired
    private MBankDepositRepo mBankDepositRepo;


    public Page<BankRes> findBanks(Pageable pageable) {
        Page<MBankEntity> entities = bankRepo.findAll(pageable);

        return new PageImpl<>(
                entities.getContent().stream().map(this::toBankDto).collect(Collectors.toList()),
                entities.getPageable(),
                entities.getTotalElements()
        );
    }

    public List<BankDeposit> getBankDeposits(){
        return mBankDepositRepo.findAllByIsRobot().stream().map(this::toBankDeposit).collect(Collectors.toList());
    }
    
    public List<BankDeposit> getBankByDepositType(String depositType){
    	return mBankDepositRepo.findAllByStatusAndDepositType("1", depositType).stream().map(this::toBankDeposit).collect(Collectors.toList());
    }
    
    public List<BankDeposit> getBankByStatusAndIsBank(){
    	return mBankDepositRepo.findAllByStatusAndIsBank("1", true).stream().map(this::toBankDeposit).collect(Collectors.toList());
    }

    private BankDeposit toBankDeposit(MBankDepositEntity entity){
        String imagePath = null;
        if (entity.getBankId().getImagePath() != null) {
            imagePath = PREFIX_PATH_IMAGE_BANK + entity.getBankId().getImagePath().substring(entity.getBankId().getImagePath().lastIndexOf("/") + 1);
        }
        return BankDeposit.builder()
                .bankDepCode(entity.getBankDepCode())
                .bankCode(entity.getBankId().getBankId())
                .bankName(entity.getBankId().getBankCode())
                .accountNo(entity.getAccNo())
                .accountName(entity.getAccName())
                .bankFullName(entity.getBankId().getName())
                .minimalTransaction(entity.getMinNominal())
                .imagePath(imagePath)
                .isRobot(entity.getIsRobot())
                .build();
    }

    private BankRes toBankDto(MBankEntity entity) {
        String imagePath = null;
        if (entity.getImagePath() != null) {
            imagePath = PREFIX_PATH_IMAGE_BANK + entity.getImagePath().substring(entity.getImagePath().lastIndexOf("/") + 1);
        }
        return BankRes.builder()
                .bankCode(entity.getBankCode())
                .bankName(entity.getName())
                .image(imagePath)
                .build();
    }

	public String getPathImage(String kode) {
		// TODO Auto-generated method stub
		MBankEntity bank = bankRepo.findByBankCode(kode).get();
		return bank.getImagePath();
	}

	public List<BankRes> findAll() {
		// TODO Auto-generated method stub
		List<BankRes> lsBank = new ArrayList<BankRes>();
		lsBank =  getBanks(lsBank);
		return lsBank;
	}

	private List<BankRes> getBanks(List<BankRes> lsBank) {
		for (MBankEntity bank: bankRepo.findAll()) {
			lsBank.add(BankRes.builder()
					.bankCode(bank.getBankCode())
					.bankName(bank.getName())
					.image("/api/bank/logo/"+bank.getBankCode())
					.build());
		}
		return lsBank;
	}
	
}


