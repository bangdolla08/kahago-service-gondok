package com.kahago.kahagoservice.enummodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum PaymentEnum {
    PENDING(0,"Pending","Booking Belum Terbayar", "PENDING"),
    REQUEST(1,"Request","Booking", "REQUEST"),
    ASSIGN_PICKUP(2,"Assign Pickup","Proses Pickup", "ASSIGN_PICKUP"),
    PICKUP_BY_KURIR(3,"Pickup By Kurir","Pemesanan Terpickup oleh kurir", "PICKUP_BY_KURIR"),
    ACCEPT_IN_WAREHOUSE(4,"Accept In Warehouse","Diterima Di Warehouse KAHA", "ACCEPT_IN_WAREHOUSE"),
    BAGGING(5,"Outgoing Manifest","Proses Pengelompokkan Barang", "BAGGING"),
    SEND_TO_VENDOR(6,"Send To Vendor","Pengiriman Ke Vendor", "SEND_TO_VENDOR"),
    PICK_BY_VENDOR(7,"Pick By Vendor","Diambil oleh Vendor", "PICK_BY_VENDOR"),
    ACCEPT_BY_VENDOR(8, "Accept By Vendor","Diterima Oleh Vendor", "ACCEPT_BY_VENDOR"),
    RECEIVE(9,"Receive","Barang Telah diterima", "RECEIVE"),
    CANCEL_BY_USER(10,"Cancel By User","Dibatalkan oleh User", "CANCEL_BY_USER"),
    HOLD_BY_WAREHOUSE(11, "Hold By Warehouse","Barang Bermasalah", "HOLD_BY_WAREHOUSE"),
    CANCEL_BY_WAREHOUSE(12,"Cancel By Warehouse","Dibatalkan oleh Warehouse KAHA", "CANCEL_BY_WAREHOUSE"),
    REJECT_BY_VENDOR(13, "Reject By Vendor","Di Tolak oleh switcherEntity", "REJECT_BY_VENDOR"),
    REJECT_BY_RECEIVER(14, "Reject By Receiver","Ditolak oleh penerima", "REJECT_BY_RECEIVER"),
    RETURN_TO_KAHAGO(15,"Return To KahaGo","Barang dikembalikan ke warehouse KAHAGO", "RETURN_TO_KAHAGO"),
    RETURN_RECEIVE(16,"Return Receive","Barang dikembalikan ke pengirim", "RETURN_RECEIVE"),
	EXPIRED_PAYMENT(17,"Expired Payment","Permbayaran telah kadaluarsa", "EXPIRED_PAYMENT"),
    RECEIVE_BY_MITRA(18,"Receive By Mitra","Barang Diterima Mitra", "RECEIVE_BY_MITRA"),
    AWAITING_PAYMENT_VENDOR(19,"Awaiting Payment Vendor","Menunggu Pembayaran Dari Vendor", "AWAITING_PAYMENT_VENDOR"),
    RECEIVE_IN_WAREHOUSE(20,"Receive In Warehouse", "Barang Diterima Warehouse KAHA", "RECEIVE_IN_WAREHOUSE"),
    UNPAID_RECEIVE(21,"Unpaid Receive","Barang Diterima Belom Terbayar", "UNPAID_RECEIVE"),
    FINISH_INPUT_AND_PAID(22,"Request Request Pickup","PICKUP AND IN WAREHOUSE", "FINISH_INPUT_AND_PAID"),
    DRAFT_PICKUP(23,"DRAFT PICKUP","DRAFT PICKUP", "DRAFT_PICKUP"),
    ACCEPT_WITHOUT_RESI(24,"Accept Without Resi","Diterima tanpa Resi", "ACCEPT_WITHOUT_RESI"),
    HOLD_BY_ADMIN(25,"Hold By Admin","Ditahan oleh admin", "HOLD_BY_ADMIN"),
    RECEIVE_IN_COUNTER(26,"Received By Counter","Diterima oleh Counter", "RECEIVE_IN_COUNTER"),
    APPROVE_BY_COUNTER(27,"Approved By Counter","Disetujui oleh Counter", "APPROVE_BY_COUNTER"),
    OUTGOING_BY_COUNTER(28,"Outgoing By Counter","Proses Pengelompokkan oleh Counter", "OUTGOING_BY_COUNTER"),
    RETUR_BY_VENDOR(29,"Retur By Vendor","Gagal Pengiriman oleh vendor", "RETUR_BY_VENDOR"),
    BAGGING_BY_COUNTER(30,"Bagging by counter","Pengelompokkan barang di konter", "BAGGING_BY_COUNTER"),
    CANCEL_BY_ADMIN(31,"Cancel by admin","Pesanan di-cancel oleh admin", "CANCEL_BY_ADMIN"),
    REJECT_BY_COURIER(32, "Reject By Courier", "Di tolak oleh Courier", "REJECT_BY_COURIER"),
    FINISH_BOOK(-1,"Finish booking from Request Pickup","Pesanan dari request pickup sudah dilengkapi", "FINISH_BOOK");

    private int code;
    private String string;
    private String keterangan;
    private String key;

    static List<PaymentEnum> all = Stream.of(PaymentEnum.values()).collect(Collectors.toList());

    PaymentEnum(int code,String string, String keterangan, String key) {
        this.code = code;
        this.string=string;
        this.keterangan=keterangan;
        this.key = key;
    }

    public static PaymentEnum getByCode(Integer value){
        return all.stream().filter(v -> v.code == value).findAny().orElseThrow(IllegalArgumentException::new);
    }

    public int getCode() {
		return code;
	}

	public String getCodeString(){
        return String.valueOf(getCode());
    }

	public void setCode(int code) {
		this.code = code;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getKeterangan() {
		return keterangan;
	}

	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
	}

    public String getKey() {
        return key;
    }

    private final static Map<Integer, PaymentEnum> map =
            Arrays.stream(PaymentEnum.values()).collect(toMap(leg -> leg.code, leg -> leg));

    public static List<PaymentEnum> getPaymentEnumList(){
        List<PaymentEnum> paymentEnums=new ArrayList<>();
        paymentEnums.add(PENDING);
        paymentEnums.add(REQUEST);
        paymentEnums.add(ASSIGN_PICKUP);
        paymentEnums.add(PICKUP_BY_KURIR);
        paymentEnums.add(ACCEPT_IN_WAREHOUSE);
        paymentEnums.add(BAGGING);
        paymentEnums.add(SEND_TO_VENDOR);
        paymentEnums.add(PICK_BY_VENDOR);
        paymentEnums.add(ACCEPT_BY_VENDOR);
        paymentEnums.add(RECEIVE);
        paymentEnums.add(CANCEL_BY_USER);
        paymentEnums.add(HOLD_BY_WAREHOUSE);
        paymentEnums.add(CANCEL_BY_WAREHOUSE);
        paymentEnums.add(REJECT_BY_VENDOR);
        paymentEnums.add(REJECT_BY_RECEIVER);
        paymentEnums.add(RETURN_TO_KAHAGO);
        paymentEnums.add(RETURN_RECEIVE);
        paymentEnums.add(EXPIRED_PAYMENT);
        paymentEnums.add(RECEIVE_BY_MITRA);
        paymentEnums.add(AWAITING_PAYMENT_VENDOR);
        paymentEnums.add(RECEIVE_IN_WAREHOUSE);
        return paymentEnums;
    }

    public static List<Integer> getListApproveBook(){
        List<Integer> integerList=new ArrayList<>();
        integerList.add(PaymentEnum.REQUEST.getValue());
        integerList.add(PaymentEnum.RECEIVE_IN_WAREHOUSE.getValue());
        integerList.add(PaymentEnum.HOLD_BY_WAREHOUSE.getValue());
        integerList.add(PaymentEnum.RECEIVE.getValue());
        integerList.add(PaymentEnum.ASSIGN_PICKUP.getValue());
        integerList.add(PaymentEnum.ACCEPT_IN_WAREHOUSE.getValue());
        integerList.add(PaymentEnum.BAGGING.getValue());
        integerList.add(PaymentEnum.SEND_TO_VENDOR.getValue());
        integerList.add(PaymentEnum.PICKUP_BY_KURIR.getValue());
        integerList.add(PaymentEnum.PICK_BY_VENDOR.getValue());
        integerList.add(PaymentEnum.ACCEPT_BY_VENDOR.getValue());
        integerList.add(PaymentEnum.DRAFT_PICKUP.getValue());
        integerList.add(PaymentEnum.RECEIVE_IN_COUNTER.getValue());
        integerList.add(PaymentEnum.APPROVE_BY_COUNTER.getValue());
        integerList.add(PaymentEnum.OUTGOING_BY_COUNTER.getValue());
        integerList.add(PaymentEnum.RETUR_BY_VENDOR.getValue());
        integerList.add(PaymentEnum.BAGGING_BY_COUNTER.getValue());
        integerList.add(PaymentEnum.HOLD_BY_ADMIN.getValue());
        integerList.add(PaymentEnum.FINISH_INPUT_AND_PAID.getValue());
        integerList.add(PaymentEnum.ACCEPT_WITHOUT_RESI.getValue());
        return integerList;
    }

    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static PaymentEnum getPaymentEnum(int code){
        return map.get(code);
    }
    /**
     * Untuk Mendapatkan Payment enum
     * @param code Int Enum
     * @return Enmum
     */
    public static PaymentEnum getPaymentEnum(String code){
        return map.get(Integer.parseInt(code));
    }

    /**
     * Get enum Integer
     * @return nilai Integer
     */
    public int getValue(){
        return code;
    }

    /**
     * Get Description String
     * @return Hasil yang lebih bisa di baca manusia
     */
    @Override
    public String toString() {
        return string;
    }
}
