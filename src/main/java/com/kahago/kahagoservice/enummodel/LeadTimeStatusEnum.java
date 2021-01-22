package com.kahago.kahagoservice.enummodel;
/**
 * @author Ibnu Wasis
 */

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum LeadTimeStatusEnum {
	PROCESS(1,"PROSES","Masih belum Diterima dan di dalam Lead Time"),
	DONE(2,"SELESAI","Diterima di dalam Lead Time"),
	DONE_LATE(3,"SELESAI TERLAMBAT","Diterima di luar Lead Time"),
	LATE(4,"TERLAMBAT","Belum Diterima di luar Lead Time"),
	STOPPED(5,"BERHENTI","Di status yang sama 3 hari atau lebih tapi masih dalam Lead Time"),
	PROBLEM(6,"BERMASALAH","Retur, Alamat tidak ditemukan dll"),
	UNDEFINED(7,"TIDAK TERDEFINISI","Balikan tidak terdefinisi");
	private int code;
	private String string;
	private String desc;
	private LeadTimeStatusEnum(int code, String string, String desc) {
		this.code = code;
		this.string = string;
		this.desc = desc;
	}
	public int getCode() {
		return code;
	}
	public String getString() {
		return string;
	}
	public String getDesc() {
		return desc;
	}
	
	private final static Map<String, LeadTimeStatusEnum> map =
            Arrays.stream(LeadTimeStatusEnum.values()).collect(toMap(leg -> leg.string, leg -> leg));
	
	public static List<String> getListStatusString(){
		List<String> result = new ArrayList<String>();
		result.add(PROCESS.string);
		result.add(DONE.string);
		result.add(DONE_LATE.string);
		result.add(LATE.string);
		result.add(STOPPED.string);
		result.add(PROBLEM.string);
		result.add(UNDEFINED.string);
		return result;		
	}
	
	public static LeadTimeStatusEnum getLeadTimeStatusEnum(String string){
        return map.get(string);
    }
}
