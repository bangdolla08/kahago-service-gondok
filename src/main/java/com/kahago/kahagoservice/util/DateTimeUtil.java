package com.kahago.kahagoservice.util;

import com.kahago.kahagoservice.entity.MPickupTimeEntity;

import lombok.SneakyThrows;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
    public static String getTime(String time) {
        String times="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

            Date dt = sdf.parse(time);
            times = sdf2.format(dt);
        } catch (Exception T){
            logger.error("Parsing Error Get time :"+time);
        }


        return times;
    }
    
    
    public static String getDateTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}
    public static String getMstodate(Long milisecond,String format) {
		return new SimpleDateFormat(format).format(new Date(milisecond));
	}
	public static java.sql.Date getSqlDate(){
        return new java.sql.Date(new Date().getTime());
    }

    public static LocalDateTime getDateFromString(String date, String format) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(date,formatter);
    }
	public static String toString(LocalDate localDate){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return localDate.format(formatter);
	}
	public static String toString(LocalTime localDate){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return localDate.format(formatter);
	}


	public static String toString(MPickupTimeEntity entity){
    	return DateTimeUtil.toString(entity.getTimeFrom()).concat("-").concat(DateTimeUtil.toString(entity.getTimeTo()));
	}
	@SneakyThrows
    public static LocalDate getDateFrom(String date, String format) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date,formatter);
    }

	/**
	 * To Change from ddmmyyyy
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static LocalDate getDateFrom(String date) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		LocalDate localDate=null;
		if(date!=null&&!date.isEmpty())
			localDate=LocalDate.parse(date,formatter);
		return localDate;
	}
	
	
    
    public static String getString2Date(String tgl,String from, String to) {
		SimpleDateFormat fromUser = new SimpleDateFormat(from);
		SimpleDateFormat myFormat = new SimpleDateFormat(to);
		String dt = null;
		try {
			dt = myFormat.format(fromUser.parse(tgl));
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return dt;
	}



    public static String TimePickup(String pickupDate,String timefrom,String timeTo) {
        String result = DateTimeUtil.getString2Date(pickupDate,
                "yyyy-MM-dd", "dd MMMMM yyyy")
                .concat(" ")
                .concat(DateTimeUtil.getString2Date(timefrom,
                        "HH:mm", "HH:mm"))
                .concat(" - ")
                .concat(DateTimeUtil.getString2Date(timeTo,
                        "HH:mm", "HH:mm"));
        return result;
    }

    public static Date getDateTime(String format,String tgl) throws ParseException {
		return new SimpleDateFormat(format).parse(tgl);
	}
	
	public static String getTimeCustom(String waktu, int menit, String format) throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateTime(format, waktu));
		cal.add(Calendar.MINUTE, menit);
		
		return new SimpleDateFormat(format).format(cal.getTime());
	}
	public static String getTimeDiff(String sDate,String eDate) {
		String result = "";
		Duration durr = null;
		try {
			LocalDateTime date1 = DateTimeUtil.getDateFromString(sDate, "yyyy-MM-dd HH:mm:ss");
			LocalDateTime date2 = DateTimeUtil.getDateFromString(eDate, "yyyy-MM-dd HH:mm:ss");
			durr = Duration.between(date1, date2);
			result = String.valueOf(durr.toMillis());
			System.out.println(durr.toMillis());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		result = DateTimeUtil.getMstodate(durr.toMillis(), "HH:mm:ss");
		result = DurationFormatUtils.formatDuration(durr.toMillis(), "HH:mm:ss");
		return result;
	} 
	
	public static String getTimetoString(LocalDateTime dateTime,String format) {
		DateTimeFormatter formater = DateTimeFormatter.ofPattern(format);
		String result = dateTime.format(formater);
		return result;
	}
	
	public static Date getDatetoString(LocalDate date,String format) throws ParseException{
		DateTimeFormatter formater = DateTimeFormatter.ofPattern(format);
		String result = date.format(formater);
		return getDateTime(format, result);
	}
	
	public static String getNameDay(LocalDate date) {
		String day = "";
		switch (date.getDayOfWeek().name().toLowerCase()) {
		case "sunday":
			day = "Minggu";
			break;
		case "monday":
			day = "Senin";
			break;
		case "tuesday":
			day = "Selasa";
			break;
		case "wednesday":
			day = "Rabu";
			break;
		case "thursday":
			day = "Kamis";
			break;
		case "friday":
			day = "Jum'at";
			break;
		default:
			day = "Sabtu";
			break ;
		}
		return day;
	}
	public static LocalTime getTimeFrom(String time,String format)throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalTime.parse(time, formatter);
	}

	public static Integer getWeakNumber(LocalDate localDate){
		Calendar calendar =  new GregorianCalendar(localDate.getYear(),localDate.getMonthValue()-1,localDate.getDayOfMonth());
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
//		Integer thisWeekNumber=
		return calendar.get(Calendar.WEEK_OF_MONTH);
	}
	public static Integer getMaxWeakNumber(LocalDate localDate){
		Calendar calendar =  new GregorianCalendar(localDate.getYear(),localDate.getMonthValue()-1,localDate.getDayOfMonth());
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
//		Integer thisWeekNumber=
		return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
	}



}
