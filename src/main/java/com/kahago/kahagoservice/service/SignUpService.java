package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.broadcast.MailerComponent;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.SignUpReq;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.util.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import javax.mail.MessagingException;
import java.time.LocalDateTime;

/**
 * @author Hendro yuwono
 */
@Service
public class SignUpService {

    @Autowired
    MailerComponent mailerComponent;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private MUserRepo userRepo;

    @Autowired
    private MUserCategoryRepo userCategoryRepo;

    @Transactional
    public void saveRegister(SignUpReq request) throws MessagingException {
        String randomString = CommonConstant.randomString(5);
        MUserEntity user = userRepo.getMUserEntitiesBy(request.getEmail());
        if (request.getReferenceNumber() != null) {
            if (!userRepo.existsByAccountNo(request.getReferenceNumber())) {
                throw new NotFoundException("reference number not found, please try again");
            }
        }
        if(user!=null) {
        	throw new NotFoundException("Email telah terdaftar");
        }

        String randomPassword = encoder.encode(randomString);

        MUserEntity entity = new MUserEntity();
        entity.setUserId(request.getEmail().replace(" ", ""));
        entity.setPassword(randomPassword);
        entity.setName(request.getFullName());
        entity.setEmail(request.getEmail().replace(" ", ""));
        entity.setAccountNo(validAccountNumber());
        entity.setUserLevel(1);
        entity.setAccountType("2");
        entity.setStatusLayanan("1");
        entity.setRegisterDate(LocalDateTime.now());
        entity.setRefNum(request.getReferenceNumber());
        entity.setHp(request.getNoHp());

        entity.setStatusLogin("1");
        entity.setStatusLayanan("1");
        entity.setExpPassword(LocalDateTime.now());
        entity.setAccountType("1");
        entity.setAreaOriginId("SUB");
        entity.setLastUpdate(LocalDateTime.now());
        entity.setLastTimeSession(LocalDateTime.now());
        entity.setBalance(new BigDecimal(0));
        entity.setDepositType("0");
        entity.setCreditDay("0");
        entity.setCourierFlag(0);
        entity.setQtyDeposit(0);
        entity.setMitraFlag(0);
        entity.setIsBranch(0);
        entity.setUserCategory(userCategoryRepo.findById(1).orElseThrow(() -> new NotFoundException("User Category is Not found")));

        userRepo.save(entity);
        mailerComponent.sendPasswordSignUp(entity.getUserId(), randomString, entity.getEmail());
    }

    public String validAccountNumber() {
        String accountNo = CommonConstant.randomString(5);

        while (userRepo.existsByAccountNo(accountNo)) {
            accountNo = CommonConstant.randomString(5);
        }

        return accountNo;
    }
}
