package com.kahago.kahagoservice.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.DeviceEnum;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;

/**
 * @author Hendro yuwono
 */
@Slf4j
public class Common {
    public static String getCounter(String count,int from,int to) {
        String parsing = count.substring(from, to);
        int hit = Integer.valueOf(parsing) + 1;
        String ahit = String.valueOf(hit);
        parsing = parsing.substring(0, parsing.length() - ahit.length());
        return parsing + ahit;
    }

    public static DecimalFormatSymbols formatLocalMoney() {
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        return formatRp;
    }

    /***
     * Membandingkan 2 Parameter Apakah Decimal Yang A lebih Besar Daripada Yang B
     * @param bigDecimalA Parameter Yang ingin Lebih Besar
     * @param bigDecimalB parameter yang jadi pembanding
     * @return
     */
    public static Boolean moreThan(BigDecimal bigDecimalA,BigDecimal bigDecimalB){
        Double aDouble=bigDecimalA.doubleValue();
        Double aDouble1=bigDecimalB.doubleValue();
        return aDouble>aDouble1;
    }
    public static String gerQrCode() {
   	 String random = String.valueOf((long) (Math.random() * Math.pow(10, 5)));
   	 String digit = UniqueRandom.calculateCheckDigit(random);
   	 String qrcode = random + digit;
   	return qrcode;
    }
    
    public static String getCurrIDR(Double nominal) {
		DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        
        return kursIndonesia.format(nominal);
	}
    
    @SneakyThrows
    public static String json2String(Object obj) {
    	return new ObjectMapper().writeValueAsString(obj);
    }
    
    public static String getResi(TPaymentEntity payment) {
		// TODO Auto-generated method stub
		String urlResi = "";
		urlResi += "api/resi/kahago?bookingcode="+payment.getBookingCode();
		urlResi += "&userid="+payment.getUserId().getUserId();
		return urlResi;
	}
    
    public static DeviceEnum getDevice(String req) {
		// TODO Auto-generated method stub
		log.info("==> Booking Header <==");
		log.info(req);
		String header = req;
		if(header.toUpperCase().contains("ANDROID")) {
			return DeviceEnum.ANDROID;
		}else if(header.toUpperCase().contains("IOS")) {
			return DeviceEnum.IOS;
		}
		return DeviceEnum.WEB;
	}
    
    public static String getString(String result) {
		return result.replace("'", "").replace("\n", " ").replace(",", " ").replace(";", " ").replace("&", "dan");
	}

    public static <T> List<T> paginate(PageRequest page, List<T> list) {
        int first = (page.getPageNumber()) * page.getPageSize();
        int last = first + page.getPageSize();

        if (last > list.size()) {
            last = list.size();
        }

        if (first > last) {
            first = last;
        }

        return list.subList(first, last);
    }
}
