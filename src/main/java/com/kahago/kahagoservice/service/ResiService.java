package com.kahago.kahagoservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.kahago.kahagoservice.entity.MModaEntity;
import com.kahago.kahagoservice.entity.TMapLayananEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.projection.CountingProductProj;
import com.kahago.kahagoservice.repository.MModaRepo;
import com.kahago.kahagoservice.repository.TManifestPosRepo;
import com.kahago.kahagoservice.repository.TMapLayananRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hendro yuwono
 */
@Service
public class ResiService {

    @Autowired
    private TPaymentRepo paymentRepo;

    @Autowired
    private TManifestPosRepo manifestPosRepo;
    @Autowired
    private TMapLayananRepo layananRepo;
    @Autowired
    private MModaRepo modaRepo;

    public Map mappingResiTiki(String bookingCode, String userId) {
        Map<String, Object> params = new HashMap<>();
        TPaymentEntity entity = paymentRepo.findByBookingCodeAndUserIdForResiTiki(bookingCode, userId, "book").orElseThrow(() -> new NotFoundException("Not Found"));

        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatter.setDecimalFormatSymbols(Common.formatLocalMoney());
        params.put("jeniskiriman", entity.getProductSwCode().getName());
        params.put("sender_name", entity.getSenderName());
        params.put("sender_address", entity.getSenderAddress());
        params.put("sender_city", entity.getOrigin());
        params.put("sender_telp", entity.getSenderTelp());
        params.put("recv_name", entity.getReceiverName());
        params.put("recv_address", entity.getReceiverAddress());
        params.put("recv_city", entity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName());
        params.put("recv_telp", entity.getReceiverTelp());
        params.put("recv_postalcode", entity.getIdPostalCode().getPostalCode());

        params.put("koli", "1 of "+ entity.getJumlahLembar());
        params.put("length", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getLength())).sum() + " cm");
        params.put("width", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getWidth())).sum() + " cm");
        params.put("height", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getHeight())).sum() + " cm");
        String stt = entity.getStt();
        if (stt.isEmpty() || !stt.equals("-")) {
            params.put("resibarcode", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 400, 50))));
            params.put("noresi", stt);
        }
        String dateTrans = entity.getTrxDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeTrans = DateTimeUtil.getString2Date(entity.getTrxTime(), "HHmm", "HH:mm");
        params.put("trxtime", timeTrans);
        params.put("trxdate",dateTrans);
        params.put("goodsdesc", entity.getGoodsDesc());
        params.put("totalprice", "0");
        params.put("weight", entity.getGrossWeight().toString());
        params.put("volume",  entity.getVolume().toString());
        params.put("goodsdesc", entity.getComodity());

        String kantorkiriman = Optional.ofNullable(entity.getProductSwCode().getSwitcherEntity().getVendorProperties().get(0).getClientCode()).orElse("");
        params.put("kantorkiriman", kantorkiriman);
        params.put("kantortujuan", kantorkiriman.substring(0, 3).concat(" - ").concat(entity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getTlc()));
        params.put("origin", kantorkiriman.substring(0, 5));
        params.put("bookcode", entity.getBookingCode());
        return params;
    }

    public Map mappingResiKahago(String bookingCode, String userId,String isBoc) {
        Map<String, Object> params = new HashMap<>();
        TPaymentEntity entity = paymentRepo.findByBookingCodeAndUserIdForResiKahago(bookingCode, userId).orElseThrow(() -> new NotFoundException("Not Found"));
        String telp = "-";
//        if(entity.getStatus() > PaymentEnum.REQUEST.getCode()) {
        	telp = entity.getReceiverTelp();
//        }
        if(Integer.valueOf(isBoc) > 0 && entity.getStatus() <= PaymentEnum.REQUEST.getCode()) telp ="-";
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatter.setDecimalFormatSymbols(Common.formatLocalMoney());

        params.put("origin", entity.getOrigin());
        params.put("destination", entity.getDestination());
        params.put("idbook", entity.getBookingCode());
        params.put("namasender", entity.getSenderName());
        params.put("alamatsender", entity.getSenderAddress());
        params.put("kotasender", "Surabaya");
        params.put("telpsender", entity.getSenderTelp());
        params.put("namareceiver", entity.getReceiverName());
        params.put("alamatreceiver", entity.getReceiverAddress());
        params.put("kelurahanreceiver", entity.getIdPostalCode().getKelurahan());
        params.put("kecamatanreceiver", entity.getIdPostalCode().getKecamatanEntity().getKecamatan());
        params.put("kotareceiver", entity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName());
        params.put("kodepos", entity.getIdPostalCode().getPostalCode());
        params.put("telpreceiver", telp);

        Long betterWeight = Math.max(entity.getGrossWeight(), entity.getVolume());
        Double packingWeight = betterWeight + entity.getTotalPackKg();
        params.put("qty", String.valueOf(entity.getJumlahLembar()));
        params.put("weight", String.valueOf(packingWeight));
        params.put("amount", formatter.format(entity.getPriceKg()));
        params.put("price", formatter.format(entity.getPrice()));
        params.put("pricerepack", formatter.format(0));
        params.put("pricepack", formatter.format(entity.getExtraCharge()));
        params.put("asuransi", formatter.format(entity.getInsurance()));
        params.put("totalprice", formatter.format(entity.getAmount().add(entity.getDiscountValue())));
        params.put("nota", entity.getNote());
        params.put("vol", String.valueOf(entity.getVolume()));
        params.put("grossweigth", String.valueOf(entity.getGrossWeight()));
        params.put("goodsdesc", entity.getGoodsDesc());
        params.put("komoditi", entity.getComodity());
        params.put("produk", entity.getProductSwCode().getName());
        params.put("pl", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getLength())).sum() + " cm");
        params.put("lw", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getWidth())).sum() + " cm");
        params.put("th", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getHeight())).sum() + " cm");
        params.put("imgvendor", entity.getProductSwCode().getSwitcherEntity().getImg());
        params.put("leadtime", entity.getProductSwCode().getSwitcherEntity().getImg());
        String stt = entity.getStt();
        if (!stt.isEmpty() || !stt.equals("-")) {
            Integer swcode = entity.getProductSwCode().getSwitcherEntity().getSwitcherCode();
            params.put("idbookvendor", stt);
            if(swcode!=301) {
            	params.put("imgbarcode", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 500, 100))));
                params.put("imgbarcodesender", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 500, 100))));
                params.put("imgbookgoods", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 500, 100))));
            }
        }
//        if (!entity.getQrcode().equals("-")) {
//            params.put("imgqrcode", new ByteArrayInputStream(Objects.requireNonNull(getQRCodeImage(entity.getQrcode(), 300, 300))));
//            params.put("qrcodetext", entity.getQrcode());
//        }

        String dateTrans = entity.getTrxDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        params.put("tgltrx", dateTrans);
        String hasil = "";
        if (!entity.getInsurance().equals(BigDecimal.ZERO)) {
            hasil = "Asuransi";
        }
        if (!entity.getExtraCharge().equals(BigDecimal.ZERO)) {
            if (hasil.equals("")) {
                hasil = "Packing Kayu";
            } else {
                hasil = hasil + ", " + "Packing Kayu";
            }
        }
        if (hasil.equals("")) {
            hasil = "-";
        }
        params.put("additional", hasil);

        return params;
    }

    public Map mappingResiWahana(String bookingCode, String userId) {
        Map<String, Object> params = new HashMap<>();
        TPaymentEntity entity = paymentRepo.findByBookingCodeAndUserIdForResiKahago(bookingCode, userId).orElseThrow(() -> new NotFoundException("Not Found"));

        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatter.setDecimalFormatSymbols(Common.formatLocalMoney());

        TMapLayananEntity mapLayanan = layananRepo.findByIdPostalCodeAndSwitcherCode(entity.getIdPostalCode().getIdPostalCode(), entity.getProductSwCode().getSwitcherEntity().getSwitcherCode());
        params.put("origin", entity.getOrigin());
        params.put("destination", entity.getDestination());
        params.put("idbook", entity.getBookingCode());
        params.put("routingcode", entity.getDatarekon());
        params.put("namasender", entity.getSenderName());
        params.put("alamatsender", entity.getSenderAddress());
        params.put("kotasender", "Surabaya");
        params.put("telpsender", entity.getSenderTelp());

        params.put("namareceiver", entity.getReceiverName());
        params.put("alamatreceiver", entity.getReceiverAddress());
        params.put("kelurahanreceiver", entity.getIdPostalCode().getKelurahan());
        params.put("kecamatanreceiver", entity.getIdPostalCode().getKecamatanEntity().getKecamatan());
        params.put("kotareceiver", entity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName());
        params.put("kodepos", entity.getIdPostalCode().getPostalCode());
        params.put("telpreceiver", entity.getReceiverTelp());

        Long betterWeight = Math.max(entity.getGrossWeight(), entity.getVolume());
        Double packingWeight = betterWeight + entity.getTotalPackKg();
        params.put("qty", String.valueOf(entity.getJumlahLembar()));
        params.put("weight", String.valueOf(packingWeight));
        params.put("amount", formatter.format(entity.getPriceKg()));
        params.put("price", formatter.format(entity.getPrice()));
        params.put("pricerepack", formatter.format(0));
        params.put("pricepack", formatter.format(entity.getExtraCharge()));
        params.put("asuransi", formatter.format(entity.getInsurance()));
        params.put("totalprice", formatter.format(entity.getAmount()));

        params.put("nota", entity.getNote());
        params.put("vol", String.valueOf(entity.getVolume()));
        params.put("grossweigth", String.valueOf(entity.getGrossWeight()));

        params.put("goodsdesc", entity.getGoodsDesc());
        params.put("komoditi", entity.getComodity());
        params.put("produk", entity.getProductSwCode().getName());
        params.put("pl", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getLength())).sum() + " cm");
        params.put("lw", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getWidth())).sum() + " cm");
        params.put("th", entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getHeight())).sum() + " cm");
        params.put("imgvendor", entity.getProductSwCode().getSwitcherEntity().getImg());
        params.put("leadtime", entity.getProductSwCode().getSwitcherEntity().getImg());
        if(mapLayanan!=null) {
        	MModaEntity moda = modaRepo.findByIdModa(mapLayanan.getIdModa());
        	params.put("moda", "VIA "+moda.getNamaModa().toUpperCase());
        }
        
        String stt = entity.getStt();
        if (stt.isEmpty() || !stt.equals("-")) {
            params.put("imgbarcode", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 500, 100))));
            params.put("imgbarcodesender", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 500, 100))));
            params.put("imgbookgoods", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 500, 100))));
            params.put("idbookvendor", stt);
        }
        String dateTrans = entity.getTrxDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        params.put("tgltrx", dateTrans);

        return params;
    }

    public Map mappingResiPos(String bookingCode, String userId, String officerId) {
        Map<String, Object> params = new HashMap<>();
        TPaymentEntity entity = paymentRepo.findByBookingCodeAndUserIdForResiKahago(bookingCode, userId).orElseThrow(() -> new NotFoundException("Not Found"));

        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatter.setDecimalFormatSymbols(Common.formatLocalMoney());

        String datarekon = entity.getDatarekon();
        params.put("jeniskiriman", datarekon.split("\\|")[1]);
        params.put("kantorkiriman", "KAHA Mansyur 60162S1");
        params.put("kantortujuan", datarekon.split("\\|")[3]);
        //sender
        params.put("sender_name", entity.getSenderName());
        params.put("sender_address", entity.getSenderAddress());
        params.put("sender_city", "Surabaya");
        params.put("sender_telp", entity.getSenderTelp());

        //penerima
        params.put("recv_name", entity.getReceiverName());
        params.put("recv_address", entity.getReceiverAddress());
        params.put("recv_city", entity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName());
        params.put("recv_telp", entity.getReceiverTelp());
        params.put("recv_postalcode", datarekon.split("\\|")[3]);

        //pricing
        String weight = String.valueOf(entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getGrossWeight())).sum() * 1000);
        String vol = String.valueOf(entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getVolWeight())).sum() * 1000);
        if(Double.valueOf(weight) < Double.valueOf(vol)) {
            weight = vol;
        }
        params.put("weight", weight);
        params.put("amount", formatter.format(entity.getPriceKg()));
        params.put("price", formatter.format(entity.getPrice()));
        params.put("diskon", formatter.format(0));
        params.put("netto", formatter.format(entity.getPrice()));
        params.put("htnb", formatter.format(entity.getHtnbPos()));
        params.put("totalprice", formatter.format(entity.getPrice().add(entity.getHtnbPos())));

        params.put("nota", entity.getNote());
        params.put("volume",  vol);
        //isi paket
        params.put("goodsdesc", entity.getGoodsDesc());
        params.put("komoditi", entity.getComodity());
        params.put("produk", entity.getProductSwCode().getName());
        params.put("length", String.valueOf(entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getLength())).sum()));
        params.put("width", String.valueOf(entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getWidth())).sum()));
        params.put("height", String.valueOf(entity.getTbooks().stream().mapToDouble(v -> Double.valueOf(v.getHeight())).sum()));
        params.put("imglogopos", entity.getProductSwCode().getSwitcherEntity().getImg());
        params.put("imglogokaha", "/home/kaha/reports/img/kahago.jpeg");
        params.put("est", "");
        params.put("bookcode", entity.getBookingCode());
        params.put("npwp", "03.064.780.4-015.000");
        params.put("userid", officerId);
        params.put("addressbranch", "Jl KH Mas Mansyur no 121 Surabaya");
        String dateTrans = entity.getTrxDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String timeTrans = DateTimeUtil.getString2Date(entity.getTrxTime(), "hhmm", "kk:mm:ss");
        params.put("postdate",dateTrans.concat(" ").concat(timeTrans));
        String stt = entity.getStt();
        if (stt.isEmpty() || !stt.equals("-")) {
            params.put("resibarcode", new ByteArrayInputStream(Objects.requireNonNull(getBarCodeImage(stt, 500, 100))));
            params.put("imgqrresi", new ByteArrayInputStream(Objects.requireNonNull(getQRCodeImage(stt, 500, 500))));
            params.put("noresi", stt);
        }
        params.put("nilaipertanggungan", formatter.format(entity.getPriceGoods()));
        params.put("tgltrx", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return params;
    }

    @Transactional
    public Map mappingManifestPos(String manifest) {
        manifestPosRepo.callSPWithManifest(manifest);
        Map<String, Object> params = new HashMap<>();

        AtomicReference<String> nokantong = new AtomicReference<>();
        AtomicInteger counter = new AtomicInteger(1);
        AtomicReference<Long> jumlah = new AtomicReference<>((long) 0);
        AtomicReference<Long> berat = new AtomicReference<>((long) 0);

        List<CountingProductProj> entities = paymentRepo.findManifest(manifest);
        for (CountingProductProj item : entities) {
            params.put("produk" + counter.get(), counter.get() + ". " + item.getOperatorSw() + " - " + item.getName() + "				: ");
            params.put("produk" + counter.get() + "_qty", item.getJumlah() + " ;");
            jumlah.updateAndGet(v -> v + item.getJumlah());
            berat.updateAndGet(v -> v + item.getWeight());
            nokantong.set(item.getKantongPos());
            counter.getAndIncrement();
        }

        params.put("tujuan", "SURABAYA 60000");
        params.put("nomanifest", manifest);
        params.put("nokantong", nokantong.get());
        params.put("tglmanifest", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("totalitem", String.valueOf(jumlah.get()));
        params.put("totalberat", String.valueOf(berat.get() * 1000));

        return params;
    }


    private static byte[] getBarCodeImage(String text, int width, int height) {
        try {
            Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height, hintMap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] getQRCodeImage(String text, int width, int height) {
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
