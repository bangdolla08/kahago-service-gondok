package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MMarketingTreeEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.model.request.SalesDashboardDtlRequest;
import com.kahago.kahagoservice.model.response.MonitorTransResponse;
import com.kahago.kahagoservice.model.response.SalesDashboardDtlResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.model.response.UserListRes;
import com.kahago.kahagoservice.repository.MMarketingTreeRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice.Local;

import javax.swing.*;

/**
 * @author Ibnu Wasis
 */
@Service
@Slf4j
public class DashboardSalesService {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private MUserRepo mUserRepo;
    @Autowired
    private MUserCategoryRepo mUserCategoryRepo;
    @Autowired
    private TPaymentRepo tPaymentRepo;
    @Autowired
    private MOfficeRepo mOfficeRepo;
    @Autowired
    private MMarketingTreeRepo marketingTreeRepo;
    @Autowired
    private OfficeCodeService officeCodeService;

    private static final String KEY_DAY = "day";
    private static final String KEY_WEEK = "week";
    private static final String KEY_MONTH = "month";

    public MonitorTransResponse getAllTrxByUserCategoryAndDate(List<Integer> userCategory, List<Integer> notUserCategory, String key, String statusUser, String userSales) {
        if (userCategory == null) userCategory = new ArrayList<>();
        List<MUserCategoryEntity> lUserCat = userCategory == null ? null : mUserCategoryRepo.findBySeqidIn(userCategory);
        List<MUserCategoryEntity> lUserNotCat = notUserCategory == null ? null : mUserCategoryRepo.findBySeqidIn(notUserCategory);
        MUserEntity userIdSales = mUserRepo.getMUserEntitiesBy(userSales);
        String refNum = null;
        if (userIdSales != null) {
            refNum = userIdSales.getAccountNo();
        }
        if (key.equals(KEY_DAY)) {
            return getTotalTrxPerDay(lUserCat, lUserNotCat, statusUser, refNum);
        } else if (key.equals(KEY_WEEK)) {
            return getTotalTrxPerWeek(lUserCat, statusUser, refNum, lUserNotCat);
        } else {
            return getTotalTrxPerMonth(lUserCat, statusUser, refNum, lUserNotCat);
        }
    }

    private MonitorTransResponse getTotalTrxPerDay(List<MUserCategoryEntity> userCategory, List<MUserCategoryEntity> mUserCategoryEntities, String statusUser, String refNum) {
        MonitorTransResponse result = new MonitorTransResponse();
        List<LocalDate> lDate = null;
        List<Long> listTotal = new ArrayList<Long>();
        List<Integer> lUserCategoryId = null;
        if (userCategory != null) {
            if (userCategory.size() > 0) {
                lUserCategoryId = userCategory.stream().map(MUserCategoryEntity::getSeqid).collect(Collectors.toList());
            }
        }
        List<Integer> notNeedShow = null;
        if (mUserCategoryEntities != null) {
            if (mUserCategoryEntities.size() > 0) {
                notNeedShow = mUserCategoryEntities.stream().map(MUserCategoryEntity::getSeqid).collect(Collectors.toList());
            }
        }

        if (statusUser == null) {
            for (int x = 0; x < 1; x++) {
                lDate = new ArrayList<>();
                lDate.add(LocalDate.now().minusDays(x));
//                Integer total = tPaymentRepo.getTotalTrxByStatusUserAndRefNumAndTrxDate(null, LocalDate.now().getYear(), refNum, lDate,
//                        null, lUserCategoryId, null, notNeedShow);
                Integer total = tPaymentRepo.countTPayment(lUserCategoryId, notNeedShow, refNum, lDate, null, null);
//                        null, lUserCategoryId, null, notNeedShow, null, null);
                listTotal.add(total.longValue());
            }
        } else {
            if (statusUser.equals("new")) {
                for (int x = 0; x < 1; x++) {
                    lDate = new ArrayList<>();
                    lDate.add(LocalDate.now().minusDays(x));
                    LocalDate minusMonths = LocalDate.now().minusMonths(x);
                    LocalDate startDate = minusMonths.withDayOfMonth(1);
                    LocalDate endDate = minusMonths.withDayOfMonth(minusMonths.lengthOfMonth());
                    Integer total = tPaymentRepo.getTotalTrxByStatusUserAndRefNumAndTrxDate(LocalDate.now().minusDays(x).getMonthValue(), LocalDate.now().getYear(), refNum, lDate,
                            LocalDate.now().getMonthValue(), lUserCategoryId, LocalDate.now().getYear(), notNeedShow);
                    //Integer total = tPaymentRepo.countTPayment(lUserCategoryId, notNeedShow, refNum, lDate, startDate, endDate);
                    listTotal.add(total.longValue());
                }
            } else if (statusUser.equals("old")) {
                for (int x = 0; x < 1; x++) {
                    lDate = new ArrayList<>();
                    lDate.add(LocalDate.now().minusDays(x));
                    String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
                    Integer total = tPaymentRepo.getTotalTrxByStatusUserOldAndRefNumAndTrxDate(monthYear, LocalDate.now().getYear(), refNum, lDate, null, lUserCategoryId);
                    listTotal.add(total.longValue());
                }
            }
        }


        result.setResultBy("day");
        result.setResultTotal(listTotal);

        return result;
    }

    private MonitorTransResponse getTotalTrxPerMonth(List<MUserCategoryEntity> userCategoryFilter, String statusUser, String refNum, List<MUserCategoryEntity> userCategoryEntitiesNotNeed) {
        MonitorTransResponse result = new MonitorTransResponse();
        List<Long> listTotal = new ArrayList<>();
        List<LocalDate> lDate = new ArrayList<>();
        List<Integer> lUserCategoryId = null;
        if (userCategoryFilter != null) {
            if (userCategoryFilter.size() > 0) {
                lUserCategoryId = userCategoryFilter.stream().map(MUserCategoryEntity::getSeqid).collect(Collectors.toList());
            }
        }
        List<Integer> integerListUserCategoryNot = null;
        if (userCategoryEntitiesNotNeed != null) {
            if (userCategoryEntitiesNotNeed.size() > 0) {
                integerListUserCategoryNot = userCategoryEntitiesNotNeed.stream().map(MUserCategoryEntity::getSeqid).collect(Collectors.toList());
            }
        }
        if (statusUser == null) {
            for (int x = 0; x < 2; x++) {
                lDate = new ArrayList<>();
                lDate.add(LocalDate.now().minusMonths(x));
                LocalDate minusMonths = LocalDate.now().minusMonths(x);
                LocalDate startDate = minusMonths.withDayOfMonth(1);
                LocalDate endDate = minusMonths.withDayOfMonth(minusMonths.lengthOfMonth());
//                Integer total = tPaymentRepo.getTotalTrxByStatusUserAndRefNumAndTrxDate(null, year, refNum, null, month, lUserCategoryId, null, integerListUserCategoryNot);
                Integer getTotal = tPaymentRepo.countTPayment(lUserCategoryId, integerListUserCategoryNot, refNum, null, startDate, endDate);
                listTotal.add(getTotal.longValue());
            }
        } else {
            if (statusUser.equals("new")) {
                for (int x = 0; x < 2; x++) {
                    lDate = new ArrayList<>();
                    lDate.add(LocalDate.now().minusMonths(x));
                    LocalDate minusMonths = LocalDate.now().minusMonths(x);
                    LocalDate startDate = minusMonths.withDayOfMonth(1);
                    LocalDate endDate = minusMonths.withDayOfMonth(minusMonths.lengthOfMonth());
                    Integer month = LocalDate.now().minusMonths(x).getMonthValue();
                    Integer year = LocalDate.now().minusMonths(x).getYear();
                    Integer total = tPaymentRepo.getTotalTrxByStatusUserAndRefNumAndTrxDate(month, year, refNum, null, month, lUserCategoryId, year, integerListUserCategoryNot);
                    //Integer total = tPaymentRepo.countTPayment(lUserCategoryId, integerListUserCategoryNot, refNum, lDate, startDate, endDate);
                    listTotal.add(total.longValue());
                }
            } else if (statusUser.equals("old")) {
                for (int x = 0; x < 2; x++) {
                    Integer month = LocalDate.now().minusMonths(x).getMonthValue();
                    Integer year = LocalDate.now().minusMonths(x).getYear();
                    String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
                    Integer total = tPaymentRepo.getTotalTrxByStatusUserOldAndRefNumAndTrxDate(monthYear, year, refNum, null, month, lUserCategoryId);
                    listTotal.add(total.longValue());
                }
            }
        }
        result.setResultBy("month");
        result.setResultTotal(listTotal);
        return result;
    }

    private MonitorTransResponse getTotalTrxPerWeek(List<MUserCategoryEntity> userCategory, String statusUser, String refNum, List<MUserCategoryEntity> userCategoryEntitiesNotNeed) {
        LocalDate trxDate = LocalDate.now();
        MonitorTransResponse result = new MonitorTransResponse();
        List<Long> listTotal = new ArrayList<>();
        List<Integer> lUserCategoryId = null;
        if (userCategory != null) {
            if (userCategory.size() > 0) {
                lUserCategoryId = userCategory.stream().map(MUserCategoryEntity::getSeqid).collect(Collectors.toList());
            }
        }
        List<Integer> userCategoryNotNeed = null;
        if (userCategoryEntitiesNotNeed != null) {
            if (userCategoryEntitiesNotNeed.size() > 0) {
                userCategoryNotNeed = userCategoryEntitiesNotNeed.stream().map(MUserCategoryEntity::getSeqid).collect(Collectors.toList());
            }
        }
        if (statusUser == null) {
            Integer thisWeekNumber = DateTimeUtil.getWeakNumber(trxDate);
            for (int x = 0; x < 2; x++) {
                trxDate = trxDate.minusMonths(x);
                trxDate = this.searchLocalDateSameWithWeek(trxDate, thisWeekNumber);
                if (trxDate != null) {
                    List<LocalDate> dateInWeek = getListDateInWeek(trxDate, thisWeekNumber);
                    LocalDate startDate = dateInWeek.get(0);
                    LocalDate endDate = dateInWeek.get(dateInWeek.size() - 1);
                    Integer total = tPaymentRepo.countTPayment(lUserCategoryId, userCategoryNotNeed, refNum, null, startDate, endDate);
                    listTotal.add(total.longValue());
                } else {
//                    int total =0;
                    listTotal.add(Long.valueOf(0));
                }

            }
        } else {
            if (statusUser.equals("new")) {
                Integer thisWeekNumber = DateTimeUtil.getWeakNumber(trxDate);
                for (int x = 0; x < 2; x++) {
                    trxDate = trxDate.minusMonths(x);
                    trxDate = this.searchLocalDateSameWithWeek(trxDate, thisWeekNumber);
//                    trxDate = trxDate.minusMonths(x);
                    List<LocalDate> dateInWeek = getListDateInWeek(trxDate, thisWeekNumber);
                    trxDate = LocalDate.now().minusMonths(x);
                    Collections.sort(dateInWeek);
                    LocalDate startDate = dateInWeek.get(0);
                    LocalDate endDate = dateInWeek.get(dateInWeek.size() - 1);
                    //Integer total = tPaymentRepo.countTPayment(lUserCategoryId, userCategoryNotNeed, refNum, null, startDate, endDate);
                    Integer total = tPaymentRepo.getTotalTrxByStatusUserAndRefNumAndTrxDate(trxDate.getMonthValue(), trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), lUserCategoryId, trxDate.getYear(), userCategoryNotNeed);
                    listTotal.add(total.longValue());
                }
            } else if (statusUser.equals("old")) {
            	Integer thisWeekNumber = DateTimeUtil.getWeakNumber(trxDate);
                for (int x = 0; x < 2; x++) {
                    trxDate = trxDate.minusMonths(x);
                    trxDate = this.searchLocalDateSameWithWeek(trxDate, thisWeekNumber);
                    List<LocalDate> dateInWeek = getListDateInWeek(trxDate, thisWeekNumber);
                    Integer month = LocalDate.now().minusMonths(x).getMonthValue();
                    Integer year = LocalDate.now().minusMonths(x).getYear();
                    String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
                    Integer total = tPaymentRepo.getTotalTrxByStatusUserOldAndRefNumAndTrxDate(monthYear, year, refNum, dateInWeek, month, lUserCategoryId);
                    listTotal.add(total.longValue());
                }
            }
        }
        result.setResultBy("week");
        result.setResultTotal(listTotal);

        return result;
    }

    public TotalTrxResponse getAverageTrxPerPastMonth(String statusUser) {
        LocalDate trxDate = LocalDate.now().minusMonths(1L);
        Integer total = 0;
        Integer totalAvg = 0;
        Calendar cal = Calendar.getInstance();
        List<Integer> listTotal = new ArrayList<>();
        if (statusUser.equals("all")) {
            //listTotal = tPaymentRepo.getAverageTrxByTrxDateAndOldUser(trxDate.getMonthValue(), trxDate.getYear(), null);
            total = tPaymentRepo.countByStatusAndUserCategoryAndMonth(trxDate.getMonthValue(), trxDate.getYear(), null);
            totalAvg = (int) Math.floor(total.doubleValue() / cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            listTotal = tPaymentRepo.getAverageTrxByTrxDateAndOldUser(trxDate.getMonthValue(), trxDate.getYear(), trxDate.getMonthValue());
            total = listTotal.stream().mapToInt(x -> x).sum();
            totalAvg = (int) Math.floor(total.doubleValue() / cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        return TotalTrxResponse.builder()
                .totalAllTrx(totalAvg)
                .build();
    }

    private LocalDate searchLocalDateSameWithWeek(LocalDate localDate, Integer week) {
        if (DateTimeUtil.getMaxWeakNumber(localDate) >= week) {
            if (DateTimeUtil.getWeakNumber(localDate).equals(week)) {
                return localDate;
            } else if (DateTimeUtil.getWeakNumber(localDate) > week) {
                LocalDate tmpMinusLocalDate = localDate.minusDays(1);
                if (tmpMinusLocalDate.getMonthValue() == localDate.getMonthValue()) {
                    return searchLocalDateSameWithWeek(tmpMinusLocalDate, week);
                }
                return null;
            } else if (DateTimeUtil.getWeakNumber(localDate) < week) {
                LocalDate tmpPlusLocalDate = localDate.plusDays(1);
                if (tmpPlusLocalDate.getMonthValue() == localDate.getMonthValue()) {
                    return searchLocalDateSameWithWeek(tmpPlusLocalDate, week);
                }
                return null;
            }
        }
        return null;
    }

    private List<LocalDate> getListDateInWeek(LocalDate localDate, Integer week) {
        List<LocalDate> result = new ArrayList<>();
        boolean finishFor = true;
        int number = 0;
        while (finishFor) {
            LocalDate minusDays = localDate.minusDays(number);
            if (DateTimeUtil.getWeakNumber(minusDays).equals(week)) {
                result.add(minusDays);
                number++;
            } else {
                finishFor = false;
            }
        }
        finishFor = true;
        number = 1;
        while (finishFor) {
            LocalDate plusDays = localDate.plusDays(number);
            if (DateTimeUtil.getWeakNumber(plusDays).equals(week)) {
                result.add(plusDays);
                number++;
            } else {
                finishFor = false;
            }
        }
        Collections.sort(result);
        return result;
    }

    private List<LocalDate> getDateofOneWeekInMonth(LocalDate trxDate) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate dateNow = LocalDate.now();
        Calendar now = Calendar.getInstance(new Locale("id", "ID"));
        Calendar past = Calendar.getInstance(new Locale("id", "ID"));
        now.set(trxDate.getYear(), trxDate.getMonthValue(), trxDate.getDayOfMonth());
        past.set(dateNow.getYear(), dateNow.getMonthValue(), dateNow.getDayOfMonth());
        if (now.get(Calendar.WEEK_OF_MONTH) != past.get(Calendar.WEEK_OF_MONTH)) {
            if (now.get(Calendar.WEEK_OF_MONTH) > past.get(Calendar.WEEK_OF_MONTH)) {
                trxDate = trxDate.minusDays(3);
                now.set(trxDate.getYear(), trxDate.getMonthValue(), trxDate.getDayOfMonth());
            } else {
                trxDate = trxDate.plusDays(3);
                now.set(trxDate.getYear(), trxDate.getMonthValue(), trxDate.getDayOfMonth());
            }
        }

        int month = trxDate.getMonthValue();
        int i = 1;
        for (int y = 0; y < trxDate.getDayOfWeek().getValue(); y++) {
            LocalDate date = trxDate.minusDays(i);
            if (month != date.getMonthValue()) {
                break;
            }
            log.info("Hari Ke " + now.getFirstDayOfWeek());
            result.add(date);
            i++;
        }
        i = 0;
        for (int x = trxDate.getDayOfWeek().getValue(); result.size() < 7; x++) {
            LocalDate date = trxDate.plusDays(i);
            if (month != date.getMonthValue()) {
                break;
            }
            result.add(date);
            i++;

        }
        return result;
    }

    public TotalTrxResponse getTotalUserByMinTrxDateAndRefNum(String key, String userSales) {
        MUserEntity user = mUserRepo.getMUserEntitiesBy(userSales);
        String refNum = null;
        if (user != null) {
            refNum = user.getAccountNo();
        }

        Integer total = 0;
        if (key == null) {
            total = mUserRepo.getTotalUserByMinTrxDate(null, null, null, refNum);
            return TotalTrxResponse.builder()
                    .totalAllTrx(total)
                    .build();
        }
        if (key.equals(KEY_DAY)) {
            List<LocalDate> lDate = new ArrayList<>();
            lDate.add(LocalDate.now());
            total = mUserRepo.getTotalUserByMinTrxDate(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), lDate, refNum);

        } else if (key.equals(KEY_WEEK)) {
        	Integer thisWeekNumber = DateTimeUtil.getWeakNumber(LocalDate.now());
            List<LocalDate> dateInWeek = new ArrayList<>();
            dateInWeek = getListDateInWeek(LocalDate.now(), thisWeekNumber);
            total = mUserRepo.getTotalUserByMinTrxDate(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), dateInWeek, refNum);
        } else {
            total = mUserRepo.getTotalUserByMinTrxDate(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), null, refNum);
        }

        return TotalTrxResponse.builder()
                .totalAllTrx(total)
                .build();
    }

    public Page<SalesDashboardDtlResponse> getDetailDashboardSales(SalesDashboardDtlRequest request) {
        Page<Object[]> lDtlSales = null;
        List<String> lOffice = null;
        if (request.getKey() == null) request.setKey(KEY_DAY);
        if (request.getPast() == null) request.setPast(false);
        String refNum = null;
        if (request.getUserSales() != null && !request.getUserSales().isEmpty()) {
            MUserEntity user = mUserRepo.getMUserEntitiesBy(request.getUserSales());
            refNum = user.getRefNum();
        }
        if (request.getOfficeCode() != null) {
            lOffice = new ArrayList<>();
            List<MOfficeEntity> lOfficeEntity = officeCodeService.getBranchList(request.getOfficeCode());
            for (MOfficeEntity entity : lOfficeEntity) {
                lOffice.add(entity.getOfficeCode());
            }
            if (request.getOfficeCode().equals("60000")) {
                lOffice = null;
            }
//            lOfficeEntity=lOfficeEntity.stream().map
        }
        if (request.getKey().equals(KEY_DAY)) {
            LocalDate trxDate = LocalDate.now();
            List<LocalDate> lDate = new ArrayList<>();
            lDate.add(trxDate);
            String getDate = trxDate.format(DateTimeFormatter.ofPattern("YYYY-MM"));
            if (request.getStatusUser() == null) {
                lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(null, trxDate.getYear(), refNum, lDate, trxDate.getMonthValue(), request.getIdUserCategory(), null, lOffice, request.getPageRequest());
            } else {
                if (request.getStatusUser().equals("new")) {
                    lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(trxDate.getMonthValue(), trxDate.getYear(), refNum, lDate, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
                } else if (request.getStatusUser().equals("old")) {
                    lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDateUserOld(getDate, trxDate.getYear(), refNum, lDate, trxDate.getMonthValue(), request.getIdUserCategory(), lOffice, request.getPageRequest());
                }
            }
        } else if (request.getKey().equals(KEY_WEEK)) {
            if (!request.getPast()) {
                LocalDate trxDate = LocalDate.now();
                String getDate = trxDate.format(DateTimeFormatter.ofPattern("YYYY-MM"));
                Integer thisWeekNumber = DateTimeUtil.getWeakNumber(trxDate);
//                trxDate = trxDate.minusMonths(x);
                trxDate = this.searchLocalDateSameWithWeek(trxDate, thisWeekNumber);
//                    trxDate = trxDate.minusMonths(x);
                List<LocalDate> dateInWeek = getListDateInWeek(trxDate, thisWeekNumber);
//                List<LocalDate> dateInWeek = getDateofOneWeekInMonth(trxDate);
                if (request.getStatusUser() == null) {
                    lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(null, trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), request.getIdUserCategory(), null, lOffice, request.getPageRequest());
                } else {
                    if (request.getStatusUser().equals("new")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(trxDate.getMonthValue(), trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
                    } else if (request.getStatusUser().equals("old")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDateUserOld(getDate, trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), request.getIdUserCategory(), lOffice, request.getPageRequest());
                    }
                }
            } else {
                LocalDate trxDate = LocalDate.now();
                String getDate = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
                Integer thisWeekNumber = DateTimeUtil.getWeakNumber(trxDate);
                trxDate = trxDate.minusMonths(1);
                trxDate = this.searchLocalDateSameWithWeek(trxDate, thisWeekNumber);
//                    trxDate = trxDate.minusMonths(x);
                List<LocalDate> dateInWeek = getListDateInWeek(trxDate, thisWeekNumber);
//                LocalDate trxDate = LocalDate.now().minusMonths(1);
//                List<LocalDate> dateInWeek = getDateofOneWeekInMonth(trxDate);
                if (request.getStatusUser() == null) {
                    lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(null, trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), request.getIdUserCategory(), null, lOffice, request.getPageRequest());
                } else {
                    if (request.getStatusUser().equals("new")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(trxDate.getMonthValue(), trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
                    } else if (request.getStatusUser().equals("old")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDateUserOld(getDate, trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), request.getIdUserCategory(), lOffice, request.getPageRequest());
                    }
                }
            }
        } else {
            if (!request.getPast()) {
                LocalDate trxDate = LocalDate.now();
                String getDate = trxDate.format(DateTimeFormatter.ofPattern("YYYY-MM"));
                if (request.getStatusUser() == null) {
                    lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(null, trxDate.getYear(), refNum, null, trxDate.getMonthValue(), request.getIdUserCategory(), null, lOffice, request.getPageRequest());
                } else {
                    if (request.getStatusUser().equals("new")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(trxDate.getMonthValue(), trxDate.getYear(), refNum, null, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
                    } else if (request.getStatusUser().equals("old")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDateUserOld(getDate, trxDate.getYear(), refNum, null, trxDate.getMonthValue(), request.getIdUserCategory(), lOffice, request.getPageRequest());
                    }
                }
            } else {
                LocalDate trxDate = LocalDate.now().minusMonths(1);
                String getDate = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
                if (request.getStatusUser() == null) {
                    lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(null, trxDate.getYear(), refNum, null, trxDate.getMonthValue(), request.getIdUserCategory(), null, lOffice, request.getPageRequest());
                } else {
                    if (request.getStatusUser().equals("new")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDate(trxDate.getMonthValue(), trxDate.getYear(), refNum, null, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
                    } else if (request.getStatusUser().equals("old")) {
                        lDtlSales = tPaymentRepo.getListPaymentByRefNumAndTrxDateUserOld(getDate, trxDate.getYear(), refNum, null, trxDate.getMonthValue(), request.getIdUserCategory(), lOffice, request.getPageRequest());
                    }
                }
            }
        }
        return new PageImpl<>(convertPagetoResponse(lDtlSales), lDtlSales.getPageable(), lDtlSales.getTotalElements());
    }

    private List<SalesDashboardDtlResponse> convertPagetoResponse(Page<Object[]> lDetailSales) {
        List<SalesDashboardDtlResponse> result = new ArrayList<>();
        for (Object[] dtl : lDetailSales) {
            String userId = (String) dtl[0];
            Date trxDate = (Date) dtl[1];
            BigInteger TrxTotal = (BigInteger) dtl[3];
            BigDecimal RevenueTtl = (BigDecimal) dtl[2];
            String office_code = (String) dtl[4];
            List<Integer> ltotalVendor = tPaymentRepo.getTotalVendorByUserIdAndTrxDate(userId, trxDate.toLocalDate(), office_code);
            MOfficeEntity office = null;
            if (office_code != null) {
                office = mOfficeRepo.findAllByOfficeCode(office_code);
            }
            SalesDashboardDtlResponse resSales = SalesDashboardDtlResponse.builder()
                    .userId(userId)
                    .trxDate(trxDate.toString())
                    .office(office == null ? "-" : office.getName())
                    .totalTrx(TrxTotal.intValue())
                    .totalRevenue(RevenueTtl.intValue())
                    .totalVendor(ltotalVendor.size())
                    .build();
            result.add(resSales);
        }
        return result;
    }

    public Page<SalesDashboardDtlResponse> getDetailDashboardSalesForTabSales(SalesDashboardDtlRequest request) {
        if (request.getKey() == null) request.setKey(KEY_DAY);
        Page<Object[]> lDtlforSales = null;
        List<String> lOffice = null;
        String refNum = null;
        if (request.getUserSales() != null && !request.getUserSales().isEmpty()) {
            MUserEntity user = mUserRepo.getMUserEntitiesBy(request.getUserSales());
            refNum = user.getAccountNo();
        }
        if (request.getOfficeCode() != null) {
            lOffice = new ArrayList<>();
            List<MOfficeEntity> lOfficeEntity = officeCodeService.getBranchList(request.getOfficeCode());
            for (MOfficeEntity entity : lOfficeEntity) {
                lOffice.add(entity.getOfficeCode());
            }
        }
        LocalDate trxDate = LocalDate.now();
        if (request.getKey().equals(KEY_DAY)) {
            List<LocalDate> lDate = new ArrayList<>();
            lDate.add(trxDate);
            lDtlforSales = tPaymentRepo.getDetailTotalTrxFromNewUser(trxDate.getMonthValue(), trxDate.getYear(), refNum, lDate, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
        } else if (request.getKey().equals(KEY_WEEK)) {
            List<LocalDate> dateInWeek = getDateofOneWeekInMonth(trxDate);
            lDtlforSales = tPaymentRepo.getDetailTotalTrxFromNewUser(trxDate.getMonthValue(), trxDate.getYear(), refNum, dateInWeek, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
        } else {
            lDtlforSales = tPaymentRepo.getDetailTotalTrxFromNewUser(trxDate.getMonthValue(), trxDate.getYear(), refNum, null, trxDate.getMonthValue(), request.getIdUserCategory(), trxDate.getYear(), lOffice, request.getPageRequest());
        }
        return new PageImpl<>(
                convertfromPageDetailSalesToListSalesDashboardResponse(lDtlforSales, refNum),
                lDtlforSales.getPageable(),
                lDtlforSales.getTotalElements());
    }

    private List<SalesDashboardDtlResponse> convertfromPageDetailSalesToListSalesDashboardResponse(Page<Object[]> lDtlforSales, String refNum) {
        List<SalesDashboardDtlResponse> result = new ArrayList<>();
        for (Object[] dtl : lDtlforSales) {
            Date trxDate = (Date) dtl[0];
            List<LocalDate> lDate = new ArrayList<>();
            lDate.add(trxDate.toLocalDate());
            BigInteger TrxTotal = (BigInteger) dtl[2];
            BigDecimal RevenueTtl = (BigDecimal) dtl[1];
            Integer newUserTotal = mUserRepo.getTotalUserByMinTrxDate(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), lDate, refNum);
            MOfficeEntity office = mOfficeRepo.findAllByOfficeCode((String) dtl[3]);
            SalesDashboardDtlResponse resSales = SalesDashboardDtlResponse.builder()
                    .trxDate(trxDate.toString())
                    .office(office.getName())
                    .totalTrx(TrxTotal.intValue())
                    .totalRevenue(RevenueTtl.intValue())
                    .totalNewUser(newUserTotal)
                    .build();
            result.add(resSales);
        }
        return result;
    }

    public List<UserListRes> getListUserSales(String userId) {
        List<UserListRes> result = new ArrayList<>();
        List<String> lUserId = new ArrayList<>();
        MMarketingTreeEntity marketing = marketingTreeRepo.findAllByUserId(userId);
        List<MMarketingTreeEntity> lmarket = marketingTreeRepo.findAll();
        if (marketing != null) {
            lUserId.add(marketing.getUserId());
            findExistChildSales(lmarket, marketing.getUserId(), lUserId);
        } else {
            for (MMarketingTreeEntity market : lmarket) {
                lUserId.add(market.getUserId());
            }
        }

        for (String usr : lUserId) {
            MUserEntity user = mUserRepo.getMUserEntitiesBy(usr);
            UserListRes ures = UserListRes.builder()
                    .userId(user.getUserId())
                    .userName(user.getName())
                    .userCategory(user.getUserCategory().getNameCategory())
                    .userCategoryId(user.getUserCategory().getSeqid())
                    .build();
            result.add(ures);
        }

        return result;
    }

    private void findExistChildSales(List<MMarketingTreeEntity> parent, String userId, List<String> result) {
        if (parent.size() > 0) {
            for (MMarketingTreeEntity entity : parent) {
                if (entity.getUserIdParent() != null && entity.getUserIdParent().equals(userId)) {
                    result.add(entity.getUserId());
                    findExistChildSales(parent, entity.getUserId(), result);
                }
            }
        }
    }
}
