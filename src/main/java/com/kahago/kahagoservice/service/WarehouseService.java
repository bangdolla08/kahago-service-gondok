package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.*;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.PaymentDto;
import com.kahago.kahagoservice.model.dto.PickupDto;
import com.kahago.kahagoservice.model.projection.IncomingOfGood;
import com.kahago.kahagoservice.model.request.OutgoingRequest;
import com.kahago.kahagoservice.model.request.ReceiveWarehouseRequest;
import com.kahago.kahagoservice.model.request.RequestBook;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.FooterReceiveWarehouseResponse;
import com.kahago.kahagoservice.model.response.OutgoingResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalBarangInCourierResponse;
import com.kahago.kahagoservice.model.validate.IncomingRequest;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.DateTimeUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import javax.validation.Valid;

import static com.kahago.kahagoservice.util.ImageConstant.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author bangd ON 27/11/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
@Validated
public class WarehouseService {
    @Autowired
    private TWarehouseReceiveDetailRepo warehouseReceiveDetailRepo;
    @Autowired
    private TWarehouseReceiveRepo warehouseReceiveRepo;
    @Autowired
    private TOutgoingListRepo outgoingListRepo;
    @Autowired
    private TOutgoingListDetailRepo outgoingListDetailRepo;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PickupService pickupService;
    @Autowired
    private HistoryTransactionService historyTransactionService;
    @Autowired
    private VendorService vendorService;
    @Autowired
    private ManifestPickupService manifestPickupService;
    @Autowired
    private MOfficeRepo officeRepo;
    @Autowired
    private MUserRepo mUserRepo;
    @Autowired
    private TPickupOrderRequestDetailRepo tPickupOrderRequestDtl;
    @Autowired
    private TPickupOrderRequestRepo tPickupOrderRequestRepo;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private BookService bookService;
    @Autowired
    private TOutgoingCounterDetailRepo tOutgoingCounterDetailRepo;
    @Autowired
    private TOutgoingCounterRepo tOutgoingCounterRepo;
    @Autowired
    private TPaymentRepo tPaymentRepo;
    @Autowired
    private OfficeCodeService officeCodeService;
    @Autowired
    private MAreaRepo mAreaRepo;
    @Autowired
    private THistoryBookRepo tHistoryBookRepo;
    @Autowired
	private BookCounterService bookCounterService;
    @Autowired
    private DepositBookService depositBookService;
    @Autowired
    private TLeadTimeHistoryRepo tLeadTimeHistoryRepo;
    @Autowired
    private TPickupDetailRepo pickupDetailRepo;
    @Autowired
    private TCourierPickupRepo courierPickupRepo;
    @Autowired
    private TPickupRepo pickupRepo;

    @Autowired
    private TBookRepo bookRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Value("${path.upload.outgoing}")
    private String uploadImage;
    @Value("${url.cetak.outgoing}")
    private String urlcetak;
    @Autowired
    private TLeadTimeRepo tLeadTimeRepo;

    private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);
    private final String DESCRIPTION_LEAD_TIME = "Proses Cetak Outgoing";

    public Page<BookDataResponse> listBookInCourier(ReceiveWarehouseRequest request, Pageable pageable) {
        List<BookDataResponse> lBooks = new ArrayList<>();
        List<TPickupDetailEntity> entities = pickupService.pickupByOrderRequest(request.getOfficeCode());
        List<TPaymentEntity> lPayment = paymentService.findByOfficeCode(PaymentEnum.PICKUP_BY_KURIR, PaymentEnum.FINISH_INPUT_AND_PAID);
        for (TPaymentEntity tp : lPayment) {
            BookDataResponse bres = pickupDetailToBook(tp);
            lBooks.add(bres);
        }
        for (TPickupDetailEntity dt : entities) {
            if (dt.getBookId() == null) {
                lBooks.addAll(getListPickupOrder(dt.getPickupOrderRequestEntity()));
            }
        }
        Comparator<BookDataResponse> sortDate = (a, b) -> a.getTrxDate().compareToIgnoreCase(b.getTrxDate());
        Collections.sort(lBooks, sortDate);
        int begin = 0;
        int pageNumber = pageable.getPageNumber() + 1;
        if (pageable.getPageNumber() > 0) {
            begin = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
        }
        int end = (pageable.getPageSize() * pageNumber) - 1;
        if (end > lBooks.size()) {
            end = lBooks.size();
        }
        Page<BookDataResponse> data = new PageImpl<>(
                lBooks.subList(begin, end),
                pageable,
                lBooks.size()
        );
        return data;
    }

    public Page<IncomingOfGood> incoming(String officeCode, Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        List<IncomingOfGood> joiningItems = fetchItemsInCourier(officeCode);
        return new PageImpl<>(Common.paginate(pageRequest, joiningItems), pageRequest, joiningItems.size());
    }

    public List<IncomingOfGood> fetchItemsInCourier(String officeCode) {
        Set<Integer> cityIds = officeCodeService.transformToCityIds(officeCode);

        List<IncomingOfGood> books = bookRepo.findByStatusAndCityIds(3, cityIds);
        List<IncomingOfGood> requestPickups = pickupOrderRequestDetailRepo.findByStatusAndCityIds(2, cityIds);

        return Stream.of(books, requestPickups).flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public IncomingOfGood findByQrCode(@Valid IncomingRequest request) {
        Set<Integer> cityId = officeCodeService.transformToCityIds(request.getOfficeCode());

        IncomingOfGood pieces = bookRepo.findByStatusAndCityIdsAndQrCode(3, cityId, request.getQrCode());
        if (Objects.isNull(pieces)) {
            pieces = pickupOrderRequestDetailRepo.findByStatusAndCityIdsAndQrCode(2, cityId, request.getQrCode());
        }
        return pieces;
    }

    @Transactional
    public void updatePiecesStatus(@Valid IncomingRequest request) {
        boolean existsPieces = bookRepo.existsByQrCode(request.getQrCode());
        if (existsPieces) {
            TBookEntity book = bookRepo.findByQrCode(request.getQrCode());
            book.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());

            bookRepo.save(book);
            revalidateBooking(book.getBookingCode());
        } else {
            TPickupOrderRequestDetailEntity requestDetail = pickupOrderRequestDetailRepo.findByQrCode(request.getQrCode());
            requestDetail.setStatus(RequestPickupEnum.IN_WAREHOUSE.getValue());

            pickupOrderRequestDetailRepo.save(requestDetail);
            revalidateRequestPickup(requestDetail.getOrderRequestEntity().getPickupOrderId());
        }
    }

    public void revalidateBooking(String bookingCode) {
        List<TBookEntity> books = bookRepo.findByBookingCode(bookingCode);
        boolean isAllBookInWarehouse = books.stream().allMatch( v -> {
            if (v.getStatus() == null) {
                return false;
            }
            return v.getStatus().equals(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
        });

        if (isAllBookInWarehouse) {
            TPaymentEntity payment = tPaymentRepo.findById(bookingCode).orElseThrow(NotFoundException::new);
            payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());

            tPaymentRepo.save(payment);
            updatePickupDetail(bookingCode, true);
        }
    }

    public void revalidateRequestPickup(String pickupOrderId) {
        List<TPickupOrderRequestDetailEntity> reqDetail = pickupOrderRequestDetailRepo.findByOrderRequestEntityPickupOrderId(pickupOrderId);
        boolean isAllReqPickupInWarehouse = reqDetail.stream().allMatch(v -> {
            if (v.getStatus() == null) {
                return false;
            }
            return v.getStatus().equals(RequestPickupEnum.IN_WAREHOUSE.getValue());
        });

        if (isAllReqPickupInWarehouse) {
            TPickupOrderRequestEntity reqHeader =  tPickupOrderRequestRepo.findByPickupOrderId(pickupOrderId);
            reqHeader.setStatus(RequestPickupEnum.IN_WAREHOUSE.getValue());

            tPickupOrderRequestRepo.save(reqHeader);
            updatePickupDetail(pickupOrderId, false);
        }
    }

    public void updatePickupDetail(String id, boolean isBooking) {
        TPickupDetailEntity pickupDetail;
        if (isBooking) {
            pickupDetail = pickupDetailRepo.findByBookIdBookingCode(id).orElse(null);
        } else {
            pickupDetail = pickupDetailRepo.findByPickupOrderRequestEntityPickupOrderId(id).orElse(null);
        }

        assert pickupDetail != null;
        pickupDetail.setStatus(PickupDetailEnum.IN_WAREHOUSE.getValue());

        pickupDetailRepo.save(pickupDetail);
        revalidatePickupDetail(pickupDetail.getPickupId().getIdPickup(), pickupDetail.getPickupAddrId().getPickupAddrId());
    }

    public void revalidatePickupDetail(int idPickup, int idPickupAddress) {
        List<TPickupDetailEntity> pickupDetail = pickupDetailRepo.findByPickupIdAndPickupAddr(idPickup, idPickupAddress);

        boolean isAllPickupDetailInWarehouse = pickupDetail.stream().allMatch(v -> v.getStatus().equals(PickupDetailEnum.IN_WAREHOUSE.getValue()));
        if (isAllPickupDetailInWarehouse) {
            updateCourierPickup(idPickup, idPickupAddress);
        }
    }

    public void updateCourierPickup(int idPickup, int idPickupAddress) {
        TCourierPickupEntity courierPickup = courierPickupRepo.findByPickupIdAndPickupAddressId(idPickup, idPickupAddress);
        courierPickup.setStatus(PickupCourierEnum.FINISH.getValue());
        courierPickupRepo.save(courierPickup);

        revalidateCourierPickup(idPickup);
    }

    private void revalidateCourierPickup(int idPickup) {
        List<TCourierPickupEntity> byPickupIdPickup = courierPickupRepo.findByPickupIdPickup(idPickup);
        boolean isCourierPickupInFinish = byPickupIdPickup.stream().allMatch(v -> v.getStatus().equals(PickupCourierEnum.FINISH.getValue()));
        if (isCourierPickupInFinish) {
            updatePickup(idPickup);
        }
    }

    private void updatePickup(int idPickup) {
        TPickupEntity pickup = pickupRepo.findById(idPickup).orElseThrow(NotFoundException::new);
        pickup.setStatus(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue());

        pickupRepo.save(pickup);
    }

    public Page<BookDataResponse> listBookInCourierIn(RequestBook request, Pageable pageable) {
        List<BookDataResponse> lBooks = new ArrayList<>();
        List<MAreaKotaEntity> lKota = null;
        List<MAreaEntity> lArea = null;
        List<String> lRegion = null;
        if (request.getOfficeCode() != null) {
            lRegion = officeCodeService.getBranchList(request.getOfficeCode().stream().findAny().get()).stream().map(o -> o.getRegionCode()).collect(Collectors.toList());
            lArea = mAreaRepo.findAllByAreaIdIn(lRegion);
        }
        if (lArea != null) {
            lKota = lArea.stream().map(x -> x.getKotaEntity()).collect(Collectors.toList());
        }

        List<TPickupDetailEntity> entities = pickupService.pickupByOrderRequest(lKota);
        List<Integer> lsStatus = new ArrayList<>(Arrays.asList(PaymentEnum.PICKUP_BY_KURIR.getCode(), PaymentEnum.FINISH_INPUT_AND_PAID.getCode()));
        List<TPaymentEntity> lPayment = tPaymentRepo.findAllByUserIdAndStatusInAndBookingCode(request.getUserId(), lsStatus, request.getQrCode(), request.getFilter(), request.getVendorCode(), lKota);
        for (TPaymentEntity tp : lPayment) {
            BookDataResponse bres = pickupDetailToBook(tp);
            lBooks.add(bres);
        }
        for (TPickupDetailEntity dt : entities) {
            if (dt.getBookId() == null) {
                lBooks.addAll(getListPickupOrder(dt.getPickupOrderRequestEntity()));
            }
        }
        Comparator<BookDataResponse> sortDate = (a, b) -> a.getTrxDate().compareToIgnoreCase(b.getTrxDate());
        Collections.sort(lBooks, sortDate);
        int begin = 0;
        int pageNumber = pageable.getPageNumber() + 1;
        if (pageable.getPageNumber() > 0) {
            begin = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
        }
        int end = (pageable.getPageSize() * pageNumber) - 1;
        if (end > lBooks.size()) {
            end = lBooks.size();
        }
        Page<BookDataResponse> data = new PageImpl<>(
                lBooks.subList(begin, end),
                pageable,
                lBooks.size()
        );
        return data;
    }

    /**
     * Untuk Mendapatkan dan check Detail Barang Yang akan di terima
     *
     * @param request
     * @return
     */
    public BookDataResponse checkItem(ReceiveWarehouseRequest request) {
        String cityId = "";
        if (request.getOfficeCode() != null) {
            cityId = officeCodeService.getBranchList(request.getOfficeCode()).get(0).getRegionCode();
        }
        MAreaEntity area = mAreaRepo.findByAreaName(cityId);
        PaymentDto paymentDto = paymentService.getPaymentByQrCodeStatus(request.getQrCode(), Arrays.asList(PaymentEnum.PICKUP_BY_KURIR.getCode(), PaymentEnum.FINISH_INPUT_AND_PAID.getCode(), PaymentEnum.OUTGOING_BY_COUNTER.getCode()), request.getOfficeCode());
        TPickupOrderRequestDetailEntity pickupDtl = tPickupOrderRequestDtl.findByQrCodeOrQrcodeExtAndStatus(request.getQrCode(), request.getQrCode(), RequestPickupEnum.IN_COURIER.getValue());

        if (paymentDto == null) {
            paymentDto = paymentService.getPaymentByQrCodeStatus(request.getQrCode(), Arrays.asList(PaymentEnum.ASSIGN_PICKUP.getCode(), PaymentEnum.FINISH_INPUT_AND_PAID.getCode()), request.getOfficeCode());
            if (pickupDtl != null) {
                return getDetailPickupReq(pickupDtl);
            } else if (paymentDto == null) {
                throw new NotFoundException("Qr Code Tidak Di temukan Atau Belom Di pickup Oleh Kurir Silahkan Cek kembali ");
            }
        } else if (!paymentDto.getPaymentEntity().getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName().equals(area.getKotaEntity().getName())) {
            throw new NotFoundException("Origin Tidak Sesuai !");
        }
        return paymentService.toBookDataResponse(paymentDto);
    }

    /**
     * Menerima Barang yang akan di proses dan menjadikan setatus menjadi receive in warehouse
     *
     * @param request
     * @return
     */
    public FooterReceiveWarehouseResponse receiveWarehouse(ReceiveWarehouseRequest request) {
        PaymentDto paymentDto = paymentService.getPaymentByQrCodeStatus(request.getQrCode(), Arrays.asList(PaymentEnum.PICKUP_BY_KURIR.getCode(), PaymentEnum.FINISH_INPUT_AND_PAID.getCode(), PaymentEnum.OUTGOING_BY_COUNTER.getCode()), request.getOfficeCode());
        if (paymentDto == null) {
            return receiveRequestPickup(request);
        }
        if (paymentDto.getPaymentEnum().equals(PaymentEnum.OUTGOING_BY_COUNTER)) {
            return receiveWarehouseFromCounter(request);
        }
        String cityId = "";
        if (request.getOfficeCode() != null) {
            cityId = officeCodeService.getBranchList(request.getOfficeCode()).get(0).getRegionCode();
        }
        TPickupDetailEntity pickupDetail = null;
        if (request.getCourierId() != null) {
            pickupDetail = pickupService.getPickupDetail(request.getQrCodeExt(), request.getCourierId());
            if (pickupDetail == null) {
                throw new NotFoundException("Kurir dan QRcode tidak sama");
            }
        } else {
            pickupDetail = pickupService.getPickupDetail(paymentDto.getBookCode());
        }
        MAreaEntity area = mAreaRepo.findByAreaName(cityId);
        if (!paymentDto.getPaymentEntity().getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName().equals(area.getKotaEntity().getName())) {
            throw new NotFoundException("Origin Tidak Sesuai !");
        }
        TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(paymentDto.getPaymentEntity());
        if (pickupDetail == null && paymentDto.getPaymentEntity().getQrcodeExt() != null) {
            TPickupOrderRequestDetailEntity pickupDtl = tPickupOrderRequestDtl.findByQrCodeOrQrcodeExt(paymentDto.getPaymentEntity().getQrcode(), paymentDto.getPaymentEntity().getQrcodeExt());
            pickupDetail = pickupService.findByPickupOrderReq(pickupDtl.getOrderRequestEntity());
        }

        PaymentEnum payEnum = PaymentEnum.getPaymentEnum(paymentDto.getPaymentEntity().getStatus());
        paymentDto.setPaymentEnum(PaymentEnum.RECEIVE_IN_WAREHOUSE);
        paymentDto.getPaymentEntity().setOfficeCode(request.getOfficeCode());
        FooterReceiveWarehouseResponse footerReceiveWarehouseResponse = FooterReceiveWarehouseResponse.builder()
                .courierName("")
                .pickupTime("")
                .pesananBelomDiterima(0)
                .barangBelomDiterima(0)
                .build();
        THistoryBookEntity historyBook = bookService.initializeHistoryBook(paymentDto.getBookCode(), paymentDto.getPaymentEntity().getJumlahLembar(), request.getUserId(), PaymentEnum.RECEIVE_IN_WAREHOUSE.getKeterangan(), paymentDto.getPaymentEntity().getStt());
        tHistoryBookRepo.save(historyBook);
        this.historyTransactionService.createHistory(entityPaymentHistory, paymentDto, request.getUserId());
        paymentDto = paymentService.savePayment(paymentDto);
        if (payEnum == PaymentEnum.PICKUP_BY_KURIR) {
            TWarehouseReceiveEntity warehouseReceive = null;
            TWarehouseReceiveDetailEntity warehouseReceiveDetail = null;
            warehouseReceive = createTWarehouseReceive(pickupDetail.getPickupId().getIdPickup(), request.getUserId(), request.getOfficeCode());
            warehouseReceiveDetail = new TWarehouseReceiveDetailEntity();
            warehouseReceiveDetail.setWarehouseReceiveId(warehouseReceive);
            warehouseReceiveDetail.setStatus(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode());
            warehouseReceiveDetail.setPickupDetailId(pickupDetail.getIdPickupDetail());
            warehouseReceiveDetail.setBookId(paymentDto.getPaymentEntity());
            warehouseReceiveDetail.setCreateBy(request.getUserId());
            warehouseReceiveDetail.setCreateAt(LocalDateTime.now());
            warehouseReceiveDetail.setUpdateAt(LocalDateTime.now());
            warehouseReceiveDetail.setUpdateBy(request.getUserId());
            warehouseReceiveRepo.saveAndFlush(warehouseReceive);
            warehouseReceiveDetailRepo.save(warehouseReceiveDetail);
            pickupDetail.setStatus(PickupDetailEnum.IN_WAREHOUSE.getValue());
            pickupService.savePickupDetailEntity(pickupDetail);
            PickupDto pickupDto = pickupService.createPickupDto(pickupDetail.getPickupId());
            if (pickupDto.getCountProcessed().equals(pickupDto.getCountQty())) {
                pickupDto.getPickupEntity().setStatus(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue());
            }
            pickupDto.getPickupEntity().setOfficeCode(request.getOfficeCode());
            pickupService.savePickupEntity(pickupDto.getPickupEntity());
            footerReceiveWarehouseResponse = FooterReceiveWarehouseResponse.builder()
                    .courierName(pickupDto.getPickupEntity().getCourierId().getName())
                    .pickupTime(pickupDetail.getPickupId().getTimePickupId().getTimeFrom() + " " + pickupDetail.getPickupId().getTimePickupId().getTimeTo())
                    .pesananBelomDiterima(manifestPickupService.countBookInCourier(pickupDto.getPickupEntity().getCourierId().getUserId()))
                    .barangBelomDiterima(manifestPickupService.sumJmlLembarInCourier(pickupDto.getPickupEntity().getCourierId().getUserId()))
                    .build();
            return footerReceiveWarehouseResponse;
        }

        return footerReceiveWarehouseResponse;
    }

    @Transactional
    FooterReceiveWarehouseResponse receiveRequestPickup(ReceiveWarehouseRequest request) {
        TPickupOrderRequestDetailEntity pickupReqDtl = tPickupOrderRequestDtl.findByQrCodeOrQrcodeExt(request.getQrCode(), request.getQrCode());
        Integer oldStatus = pickupReqDtl.getStatus();
        TPickupDetailEntity pickupDtl = pickupService.findByPickupOrderReq(pickupReqDtl.getOrderRequestEntity());
        List<TPickupOrderRequestDetailEntity> lPickupReqDtl = tPickupOrderRequestDtl.findAllByOrderRequestEntity(pickupDtl.getPickupOrderRequestEntity());
        pickupReqDtl.setStatus(RequestPickupEnum.IN_WAREHOUSE.getValue());
        TWarehouseReceiveEntity warehouseReceive = createTWarehouseReceive(pickupDtl.getPickupId().getIdPickup(), request.getUserId(), request.getOfficeCode());
        TWarehouseReceiveDetailEntity warehouseReceiveDetail = new TWarehouseReceiveDetailEntity();
        warehouseReceiveDetail.setWarehouseReceiveId(warehouseReceive);
        warehouseReceiveDetail.setStatus(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode());
        warehouseReceiveDetail.setPickupDetailId(pickupDtl.getIdPickupDetail());
//         warehouseReceiveDetail.setPickupOrderRequestDetailEntity(pickupReqDtl);
        warehouseReceiveDetail.setQrcodeRequest(pickupReqDtl.getQrcodeExt());
        warehouseReceiveDetail.setCreateBy(request.getUserId());
        warehouseReceiveDetail.setCreateAt(LocalDateTime.now());
        warehouseReceiveDetail.setUpdateAt(LocalDateTime.now());
        warehouseReceiveDetail.setUpdateBy(request.getUserId());
        warehouseReceiveRepo.save(warehouseReceive);
        warehouseReceiveDetailRepo.save(warehouseReceiveDetail);
        tPickupOrderRequestDtl.save(pickupReqDtl);
        historyTransactionService.historyRequestPickup(pickupReqDtl.getOrderRequestEntity(), pickupReqDtl, oldStatus, request.getUserId(), "");
        Boolean flag = true;
        for (TPickupOrderRequestDetailEntity dtl : lPickupReqDtl) {
            if (!dtl.getStatus().equals(RequestPickupEnum.IN_WAREHOUSE.getValue())) {
                flag = false;
            }
        }
        if (flag) {
            pickupReqDtl.getOrderRequestEntity().setStatus(RequestPickupEnum.IN_WAREHOUSE.getValue());
            pickupDtl.setStatus(PickupDetailEnum.IN_WAREHOUSE.getValue());
            tPickupOrderRequestRepo.save(pickupReqDtl.getOrderRequestEntity());
            pickupService.savePickupDetailEntity(pickupDtl);
        }
        PickupDto pickupDto = pickupService.createPickupDto(pickupDtl.getPickupId());
        if (pickupDto.getCountProcessed().equals(pickupDto.getCountQty())) {
            pickupDto.getPickupEntity().setStatus(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue());
        }
        pickupDto.getPickupEntity().setOfficeCode(request.getOfficeCode());
        pickupService.savePickupEntity(pickupDto.getPickupEntity());
        FooterReceiveWarehouseResponse footerReceiveWarehouseResponse = FooterReceiveWarehouseResponse.builder()
                .courierName(pickupDto.getPickupEntity().getCourierId().getName())
                .pickupTime(pickupDtl.getPickupId().getTimePickupId().getTimeFrom() + " " + pickupDtl.getPickupId().getTimePickupId().getTimeTo())
                .pesananBelomDiterima(manifestPickupService.countBookInCourier(pickupDto.getPickupEntity().getCourierId().getUserId()))
                .barangBelomDiterima(manifestPickupService.sumJmlLembarInCourier(pickupDto.getPickupEntity().getCourierId().getUserId()))
                .build();
        return footerReceiveWarehouseResponse;

    }

    /**
     * Get List Outgoing
     *
     * @param pageable
     * @param request
     * @return
     */
    public Page<OutgoingResponse> getOutgoingList(Pageable pageable, OutgoingRequest request) {
        List<Integer> filterStatus = null;
        if (request.getStatus() != null) {
            filterStatus = new ArrayList<>();
            filterStatus.add(request.getStatus());
            if (request.getStatus() == 0) {
                filterStatus.add(3);
            }
        } else {
            filterStatus = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        }
        Page<TOutgoingListEntity> listEntities = outgoingListRepo.findAllBy(request.getOfficeCode(), request.getVendorCode(), filterStatus, request.getOutgoingNumber(), pageable);
        return new PageImpl<>(
                listEntities.getContent().stream().map(this::generateOutgoingResponse).collect(Collectors.toList()),
                listEntities.getPageable(),
                listEntities.getTotalElements());
    }

    /**
     * untuk membuat Outgoing List
     *
     * @param request
     * @return
     */
    public OutgoingResponse createOutgoing(OutgoingRequest request) {
        TOutgoingListEntity outgoingListEntity = null;
        MSwitcherEntity switcherEntity = null;
        if (request.getManifestId() != null) {
            outgoingListEntity = outgoingListRepo.findByCode(request.getManifestId());
        } else {
            switcherEntity = vendorService.getSwitcherEntity(request.getVendorCode());
            outgoingListEntity = outgoingListRepo.findFirstBySwitcherEntity(switcherEntity, request.getOfficeCode(), LocalDate.now());
        }
        if (outgoingListEntity == null) {
            outgoingListEntity = TOutgoingListEntity.builder()
                    .code(createTiketWarehouse("MOT", false, switcherEntity.getSwitcherCode().toString()))
                    .isPickupVendor(false)
                    .courierName("")
                    .processBy(request.getUserId())
                    .officeCode(officeRepo.findById(request.getOfficeCode()).get())
                    .status(TOutGoingEnum.PENDING.getCode())
//                    .officeCode(request.getOfficeCode())
//                    .courierName()
                    .createBy(request.getUserId())
                    .createDate(LocalDate.now())
                    .switcherEntity(switcherEntity)
                    .build();
            outgoingListEntity = outgoingListRepo.save(outgoingListEntity);
        }
        List<TOutgoingListDetailEntity> outgoingListDetailEntities = outgoingListDetailRepo.findAllByOutgoingListId(outgoingListEntity.getIdOutgoingList());
        return generateOutgoingResponse(outgoingListEntity, outgoingListDetailEntities);
    }

    /**
     * untuk Check Book requset
     *
     * @return
     */
    public BookDataResponse checkBookRequest(OutgoingRequest request) {
        TOutgoingListEntity outgoingListEntity = outgoingListRepo.findByCode(request.getManifestId());
        if (outgoingListEntity == null)
            throw new NotFoundException("Code Outgoing tidak ditemukan");
        PaymentDto paymentDto = paymentService.getPaymentByQrCode(request.getQrCode(), PaymentEnum.ACCEPT_IN_WAREHOUSE);
        if (paymentDto == null)
            throw new NotFoundException("Qr Code Tidak Di Ditemukan");
        if (!paymentDto.getPaymentEntity().getProductSwCode().getSwitcherEntity().equals(outgoingListEntity.getSwitcherEntity()))
            throw new NotFoundException("Vendor Tidak Sama");
        TWarehouseReceiveDetailEntity warehouseReceive = this.searchWarehouseReceiveDetailEntity(paymentDto.getBookCode(), WarehouseEnum.APPROVE);
        if (warehouseReceive == null)
            throw new NotFoundException("Qr Code Tidak Di Ditemukan Atau Sudah Tersecan");
        if (!paymentDto.getPaymentEntity().getOfficeCode().equals(outgoingListEntity.getOfficeCode().getOfficeCode()))
            throw new NotFoundException("Data tidak ditemukan !!");
        return paymentService.toBookDataResponse(paymentDto);
    }

    /**
     * untuk Check Book Thumbnail
     *
     * @return
     */
    public OutgoingResponse scanThumbnail(String outGoingManifest) {
        TOutgoingListEntity outgoingListEntity = outgoingListRepo.findByCode(outGoingManifest);
        return generateOutgoingResponse(outgoingListEntity);
    }

    @Transactional
    public BookDataResponse saveBookRequest(OutgoingRequest request) {
        PaymentDto paymentDto = paymentService.getPaymentByQrCode(request.getQrCode(), PaymentEnum.ACCEPT_IN_WAREHOUSE);
        if (paymentDto == null)
            throw new NotFoundException("Qr Code Tidak Di Ditemukan");
        TWarehouseReceiveDetailEntity warehouseReceive = this.searchWarehouseReceiveDetailEntity(paymentDto.getBookCode(), WarehouseEnum.APPROVE);
        if (warehouseReceive == null)
            throw new NotFoundException("Qr Code Tidak Di Ditemukan Atau Sudah Tersecan");
        TOutgoingListEntity outgoingListEntity = outgoingListRepo.findByCode(request.getManifestId());
        if (outgoingListEntity == null)
            throw new NotFoundException("Code Outgoing tidak ditemukan");
        TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(paymentDto.getPaymentEntity());

        paymentDto.setPaymentEnum(PaymentEnum.BAGGING);
        paymentDto.getPaymentEntity().setStatus(PaymentEnum.BAGGING.getValue());
        this.historyTransactionService.createHistory(entityPaymentHistory, paymentDto.getPaymentEntity(), request.getUserId());
//        paymentService.save(payment);
        TOutgoingListDetailEntity listDetailEntity = TOutgoingListDetailEntity.builder().outgoingListId(outgoingListEntity.getIdOutgoingList())
                .warehouseReceiveDetailId(warehouseReceive.getIdWarehouseReceiveDetail())
                .bookingCode(paymentDto.getPaymentEntity())
                .tisCable(outgoingListEntity.getCode())
                .build();
        outgoingListDetailRepo.save(listDetailEntity);
        paymentService.savePayment(paymentDto);
        return paymentService.toBookDataResponse(paymentDto);
    }

    private BookDataResponse pickupDetailToBook(TPaymentEntity payment) {
        TPickupDetailEntity pickup = pickupService.getPickupDetail(payment.getBookingCode());
        BookDataResponse bookDataResponse = new BookDataResponse();
        bookDataResponse = paymentService.toBookDataResponse(payment);
        if (pickup != null) {
            bookDataResponse.setCourierName(pickup.getPickupId().getCourierId().getName());
        } else {
            if (payment.getQrcodeExt() != null && !payment.getQrcodeExt().isEmpty()) {
                TPickupOrderRequestDetailEntity pickupDtl = tPickupOrderRequestDtl.findByQrcodeExt(payment.getQrcodeExt());
                pickup = pickupService.findByPickupOrderReq(pickupDtl.getOrderRequestEntity());
                if (pickup != null) {
                    bookDataResponse.setCourierName(pickup.getPickupId().getCourierId().getName());
                } else {
                    bookDataResponse.setCourierName("");
                }
            } else {
                bookDataResponse.setCourierName("");
            }

        }

        bookDataResponse.setTrxDate(bookDataResponse.getDateTrx().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + " " + bookDataResponse.getTimeTrx());
        return bookDataResponse;
    }

    private String createTiketWarehouse(String tiketStandart, Boolean isReceive, String swictherCode) {
        Date date = Calendar.getInstance().getTime();
        String tiket = "0";
        String count = tiketStandart + "0000000";
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String a = dateFormat.format(date);
        String depositCount = null;
        if (isReceive)
            depositCount = warehouseReceiveRepo.getCodeCount(a);
        else
            depositCount = outgoingListRepo.getCodeCount(a);
        if (depositCount != null)
            count = depositCount;
        if (!isReceive) {
//            MOT2003133131
            System.out.print(count);
//            count = Common.getCounter(count,3,10);
            tiket = tiketStandart + a + count;
            return tiket;
        }
        count = Common.getCounter(count, 6, 10);
        tiket = tiketStandart + a + count;
        return tiket;
    }

    private TWarehouseReceiveEntity createTWarehouseReceive(Integer pickupId, String userId, String officeCode) {
        TWarehouseReceiveEntity tWarehouseReceive = warehouseReceiveRepo.findFirstByPickupIdOrderByIdWarehouseReceiveDesc(pickupId);
        if (tWarehouseReceive == null) {
            tWarehouseReceive = TWarehouseReceiveEntity.builder()
                    .pickupId(pickupId)
                    .code(createTiketWarehouse("WHI", false, officeCode.substring(0, 3)))
                    .createBy(userId)
                    .createDate(LocalDateTime.now())
                    .build();
        }
        tWarehouseReceive.setUpdateDate(LocalDateTime.now());
        tWarehouseReceive.setUpdateBy(userId);
        tWarehouseReceive.setOfficeCode(officeCode);
        return tWarehouseReceive;
    }

    private TWarehouseReceiveDetailEntity searchWarehouseReceiveDetailEntity(String bookingCode, WarehouseEnum warehouseEnum) {
        return warehouseReceiveDetailRepo.findFirstByBookIdAndStatus(bookingCode, warehouseEnum.getCode());
    }

    private OutgoingResponse generateOutgoingResponse(TOutgoingListEntity outgoingListEntity) {
        Integer qtyItem = outgoingListDetailRepo.countItemBook(outgoingListEntity.getIdOutgoingList()) == null ? 0 : outgoingListDetailRepo.countItemBook(outgoingListEntity.getIdOutgoingList());
        Integer sumValue = outgoingListDetailRepo.sumVolume(outgoingListEntity.getIdOutgoingList());
        Integer sumWeight = outgoingListDetailRepo.sumGrossWeight(outgoingListEntity.getIdOutgoingList());
        return OutgoingResponse.builder().codeOutgoing(outgoingListEntity.getCode())
                .officeName(outgoingListEntity.getOfficeCode().getName())
                .qtyItem(qtyItem)
                .sumVolume(sumValue == null ? 0 : sumValue)
                .sumWeight(sumWeight == null ? 0 : sumWeight)
                .officeName(outgoingListEntity.getOfficeCode().getName())
                .officeCode(outgoingListEntity.getOfficeCode().getOfficeCode())
                .vendorName(outgoingListEntity.getSwitcherEntity().getName())
                .vendorCode(outgoingListEntity.getSwitcherEntity().getSwitcherCode())
                .isEditable(CommonConstant.toBoolean(outgoingListEntity.getStatus() > 1 ? 1 : outgoingListEntity.getStatus()))
                .statusOutgoing(outgoingListEntity.getStatus())
                .statusOutgoingString(TOutGoingEnum.getPaymentEnum(outgoingListEntity.getStatus()).toString())
                .dateOutgoing(outgoingListEntity.getCreateDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                .courierId(outgoingListEntity.getCourierId() == null ? "" : outgoingListEntity.getCourierId())
                .courierPhone(outgoingListEntity.getCourierPhone() == null ? "" : outgoingListEntity.getCourierPhone())
                .courierName(outgoingListEntity.getCourierName() == null ? "" : outgoingListEntity.getCourierName())
                .isPickupVendor(outgoingListEntity.getIsPickupVendor() == null ? false : outgoingListEntity.getIsPickupVendor())
                .dateProcess(outgoingListEntity.getProcessDate() == null ? "" : outgoingListEntity.getProcessDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                .build();
    }

    private OutgoingResponse generateOutgoingResponse(TOutgoingListEntity outgoingListEntity, List<TOutgoingListDetailEntity> listDetailEntities) {
        OutgoingResponse outgoingResponse = generateOutgoingResponse(outgoingListEntity);
        if (listDetailEntities != null) {
            List<BookDataResponse> bookDataResponses = new ArrayList<>();
            for (TOutgoingListDetailEntity outgoingListDetailEntity : listDetailEntities) {
                bookDataResponses.add(paymentService.toBookDataResponse(outgoingListDetailEntity.getBookingCode()));
            }
            outgoingResponse.setBookDataResponses(bookDataResponses);
        }
        return outgoingResponse;
    }

    @Transactional
    public Response<String> saveCourierOutgoing(String courierId, Boolean isPickupVendor, String courierName, String phone, String codeOutgoing, String userIdEdit) {
        TOutgoingListEntity tOutgoing = outgoingListRepo.findByCode(codeOutgoing);
        if (tOutgoing == null) {
            throw new NotFoundException("Data Tidak Ditemukan !");
        }
        if (!isPickupVendor && courierId == null) {
            throw new NotFoundException("Courier ID tidak boleh kosong");
        }
        if (isPickupVendor && phone == null) {
            throw new NotFoundException("No telepon tidak boleh kosong");
        }

        MUserEntity courier = null;
        if (courierId != null) {
            courier = mUserRepo.getOne(courierId);
        }

        if (isPickupVendor) {
            tOutgoing.setIsPickupVendor(isPickupVendor);
            tOutgoing.setCourierId("");
            tOutgoing.setCourierName(courierName);
            tOutgoing.setCourierPhone(phone);
        } else {
            tOutgoing.setIsPickupVendor(isPickupVendor);
            tOutgoing.setCourierId(courierId);
            tOutgoing.setCourierName(courier == null ? courierName : courier.getName());
            tOutgoing.setCourierPhone(courier == null ? phone : courier.getHp());
        }
//    	tOutgoing.setUploadBy(userIdEdit);
//    	tOutgoing.setUploadDate(LocalDateTime.now());
        tOutgoing.setStatus(TOutGoingEnum.COURIER_DATA.getCode());
        outgoingListRepo.save(tOutgoing);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase()
        );
    }

    @SuppressWarnings("unused")
    @Transactional
    public Response<String> uploadOutgoing(MultipartFile file, String date, String time, String outgoingCode, String userId) {
        TOutgoingListEntity toutgoing = outgoingListRepo.findByCode(outgoingCode);
        List<TOutgoingListDetailEntity> loutgoingDtl = outgoingListDetailRepo.findAllByOutgoingListId(toutgoing.getIdOutgoingList());
        LocalDate odate = LocalDate.now();
        LocalTime otime = LocalTime.now();
        if (toutgoing == null) {
            throw new NotFoundException("Data Outgoing tidak Ditemukan !");
        }
        try {
            odate = DateTimeUtil.getDateFrom(date, "yyyy-MM-dd");
            otime = DateTimeUtil.getTimeFrom(time, "HH:mm");
            String dateInput = odate.toString() + " " + otime.toString();
            String uploadDataKableTys = loutgoingDtl.get(0).getTisCable();
            if (file != null) {
                File files = new File(uploadImage + uploadDataKableTys + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
                file.transferTo(files);
                toutgoing.setImgOutgoing(uploadImage + uploadDataKableTys + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
                toutgoing.setOutgoingDate(DateTimeUtil.getDateFromString(dateInput, "yyyy-MM-dd HH:mm"));
                toutgoing.setUploadBy(userId);
                toutgoing.setUploadDate(LocalDateTime.now());
                toutgoing.setStatus(TOutGoingEnum.UPLOAD.getCode());
                for (TOutgoingListDetailEntity odtl : loutgoingDtl) {
                    TPaymentEntity payment = paymentService.get(odtl.getBookingCode().getBookingCode());
                    PaymentDto paymentDto = paymentService.generatePaymentDto(payment);
                    TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(paymentDto.getPaymentEntity());
                    payment.setStatus(PaymentEnum.ACCEPT_BY_VENDOR.getCode());
                    this.historyTransactionService.createHistory(entityPaymentHistory, payment, payment.getUserId().getUserId());
                    paymentService.save(payment);
                }
                outgoingListRepo.save(toutgoing);
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.error(e.getMessage());
            e.printStackTrace();
            return new Response<>(
                    ResponseStatus.FAILED.value(),
                    ResponseStatus.FAILED.getReasonPhrase()
            );
        }
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase()
        );
    }

    public ResponseEntity<byte[]> getReportOutgoing(String tisCable, String userId) {
        List<TOutgoingListDetailEntity> tOutgoingDtl = outgoingListDetailRepo.findAllByTisCable(tisCable);
        if (tOutgoingDtl.size() == 0)
            return null;
        TOutgoingListEntity tOutgoing = outgoingListRepo.getOne(tOutgoingDtl.get(0).getOutgoingListId());
        MUserEntity user = mUserRepo.getMUserEntitiesBy(userId);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tOutgoing", tOutgoing.getCode());
        parameters.put("Cabang", tOutgoing.getOfficeCode().getName());
        MSwitcherEntity vendor = tOutgoingDtl.get(0).getBookingCode().getProductSwCode().getSwitcherEntity();
        parameters.put("Vendor", vendor.getDisplayName());
        parameters.put("adminName", user.getName());
        parameters.put("Tanggal", tOutgoing.getProcessDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (user.getHp() != null)
            parameters.put("adminPhone", user.getHp());
        else
            parameters.put("adminPhone", "");
        if (tOutgoing.getIsPickupVendor()) {
            parameters.put("courierName", "");
            parameters.put("courierPhone", "");
            parameters.put("courierTitle", "");
            parameters.put("vendorRecivePhone", tOutgoing.getCourierPhone());
            parameters.put("vendorReciveName", tOutgoing.getCourierName());

        } else {
            parameters.put("courierName", tOutgoing.getCourierName());
            parameters.put("courierPhone", tOutgoing.getCourierPhone());
            parameters.put("courierTitle", "KURIR KAHAGO");
            parameters.put("vendorRecivePhone", "");
            parameters.put("vendorReciveName", "");
        }
        return generateFile("printOutgoing", parameters, "pdf");
    }

    @Transactional
    public ResponseEntity<byte[]> generateFile(String reportName, Map<String, Object> parameters, String format) {
        JasperPrint jasperPrint;
        JasperReport report;
        byte[] bytes = null;
        try {
            Connection connection = dataSource.getConnection();
            report = getReportTemplate(reportName);
            jasperPrint = JasperFillManager.fillReport(report, parameters, connection);
            connection.close();
            if (format.equalsIgnoreCase("pdf")) {
                bytes = JasperExportManager.exportReportToPdf(jasperPrint);
                return ResponseEntity.ok()
                        // Specify content type as PDF
                        .header("Content-Type", "application/pdf; charset=UTF-8")
                        // Tell browser to display PDF if it can
                        .header("Content-Disposition", "inline; filename=\"" + reportName + ".pdf\"").body(bytes);
            } else if (format.equalsIgnoreCase("xlsx")) {
                JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                ByteArrayOutputStream xlsBytes = new ByteArrayOutputStream();
                SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
                reportConfig.setSheetNames(new String[]{reportName});
                reportConfig.setRemoveEmptySpaceBetweenRows(true);
                xlsxExporter.setConfiguration(reportConfig);
                xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsBytes));
                xlsxExporter.exportReport();
                bytes = xlsBytes.toByteArray();
                return ResponseEntity.ok()
                        // Specify content type as XLSX
                        .header("Content-Type", "application/vnd.ms-excel; charset=UTF-8")
                        // Tell browser to download it.
                        .header("Content-Disposition", "attachment; filename=\"" + reportName + ".xlsx\"").body(bytes);
            }
        } catch (JRException | SQLException e) {
            // TODO: handle exception
            log.error(e.getMessage());
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        return null;
    }

    private JasperReport getReportTemplate(String reportname) throws JRException {
        log.debug("report name->" + reportname);
        InputStream stream = getClass().getResourceAsStream("/reports/".concat(reportname).concat(".jrxml"));
        JasperDesign jasperDesign = JRXmlLoader.load(stream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        return jasperReport;
    }

    private List<BookDataResponse> getListPickupOrder(TPickupOrderRequestEntity entity) {
        List<BookDataResponse> ldtl = new ArrayList<>();
        List<TPickupOrderRequestDetailEntity> lPickup = tPickupOrderRequestDtl.findAllByOrderRequestEntityAndStatus(entity, RequestPickupEnum.IN_COURIER.getValue());
        for (TPickupOrderRequestDetailEntity pdt : lPickup) {
            TPickupDetailEntity pickup = pickupService.getPickupDetailByBookId(pdt.getOrderRequestEntity().getPickupOrderId());
            BookDataResponse dr = getDetailPickupReq(pdt);
            dr.setTrxDate(pdt.getCreateDate().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + " " + pdt.getCreateDate().format(DateTimeFormatter.ofPattern("HHmm")));
            if (pickup != null) {
                dr.setCourierName(pickup.getPickupId().getCourierId().getName());
            } else {
                dr.setCourierName("");
            }
            ldtl.add(dr);
        }
        return ldtl;
    }

    private BookDataResponse getDetailPickupReq(TPickupOrderRequestDetailEntity entity) {
        String images = "-";
        if (entity.getProductSwitcherEntity() != null) {
            images = PREFIX_PATH_IMAGE_VENDOR + entity.getProductSwitcherEntity().getSwitcherEntity().getImg().substring(entity.getProductSwitcherEntity().getSwitcherEntity().getImg().lastIndexOf("/") + 1);

        }
        String statusPayment = "";
//    	if(entity.getStatus()==RequestPickupEnum.ASSIGN_PICKUP.getValue()){
//
//        }
        RequestPickupEnum pickupEnum = RequestPickupEnum.getPaymentEnum(entity.getStatus());
        switch (pickupEnum) {
            case ASSIGN_PICKUP:
                statusPayment = PaymentEnum.ASSIGN_PICKUP.getCodeString();
                break;
            case REQUEST:
                statusPayment = PaymentEnum.REQUEST.getCodeString();
                break;
            case IN_WAREHOUSE:
                statusPayment = PaymentEnum.ACCEPT_IN_WAREHOUSE.getCodeString();
                break;
            case IN_COURIER:
                statusPayment = PaymentEnum.PICKUP_BY_KURIR.getCodeString();
                break;
            default:
                statusPayment = PaymentEnum.ACCEPT_IN_WAREHOUSE.getCodeString();
                break;
        }

        return BookDataResponse.builder()
                .userId(entity.getOrderRequestEntity().getUserEntity().getUserId())
                .amount(entity.getAmount() == null ? BigDecimal.ZERO : entity.getAmount())
                .bookingCode(entity.getOrderRequestEntity().getPickupOrderId())
                .dateTrx(entity.getCreateDate().toLocalDate())
                .destination(entity.getAreaId() == null ? "-" : entity.getAreaId().getKotaEntity().getName())
                .isBooking(false)
                .origin(entity.getOrderRequestEntity().getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
                .pickupAddress(entity.getOrderRequestEntity().getPickupAddressEntity().getAddress())
                .productName(entity.getProductSwitcherEntity() == null ? "-" : entity.getProductSwitcherEntity().getName())
                .receiverAddress("-")
                .receiverName(entity.getNamaPenerima() == null ? "-" : entity.getNamaPenerima())
                .shipperName("-")
                .status(statusPayment)
                .statusDesc(RequestPickupEnum.getPaymentEnum(entity.getStatus()).toString())
                .vendorUrlImage(images)
                .vendorName(entity.getProductSwitcherEntity() == null ? "-" : entity.getProductSwitcherEntity().getSwitcherEntity().getName())
                .dimension("-")
                .qty(entity.getQty())
                .weight(entity.getWeight() == null ? Long.valueOf("0") : entity.getWeight().longValue())
                .volumeWeight(Long.valueOf("0"))
                .qrcode(entity.getQrcodeExt())
                .goodDesc("-")
                .build();
    }

    @Transactional
    public SaveResponse doDeleteBookFromManifest(String userId, String manifest, String bookingCode) {
        SaveResponse savedResponse = null;
        TOutgoingListEntity outgoing = outgoingListRepo.findByCode(manifest);
        if (outgoing == null)
            throw new NotFoundException("Data Tidak Di temukan di outgoing!!!");
        if (outgoing.getStatus().equals(TOutGoingEnum.PRINT.getCode()) || outgoing.getStatus().equals(TOutGoingEnum.UPLOAD.getCode()))
            throw new NotFoundException("Data Sudah Tidak Pending Tidak Bisa update");
        TOutgoingListDetailEntity entity =
                outgoingListDetailRepo.findByBookingCodeBookingCodeAndAndOutgoingListId(bookingCode, outgoing.getIdOutgoingList());
        if (entity == null)
            throw new NotFoundException("Data Tidak Ada Book Nya!!!!!!!!!");

        TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(entity.getBookingCode());
        entity.getBookingCode().setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getValue());
        this.historyTransactionService.createHistory(entityPaymentHistory, entity.getBookingCode(), userId);
//        outgoingListRepo.save(outgoing);
//        outgoingListRepo.delete(outgoing);
        paymentService.save(entity.getBookingCode());
        this.outgoingListDetailRepo.delete(entity);
        return SaveResponse.builder()
                .saveInformation("Berhasil Save")
                .saveStatus(SaveSendStatusEnum.SAVE.getSaveNumber())
                .build();
    }

    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public SaveResponse doCetakOutgoing(String courierId, Boolean isPickupVendor, String courierName, String phone, String codeOutgoing, String codeManifest, String userId) {
        TOutgoingListEntity outgoing = outgoingListRepo.findByCode(codeManifest);
        
        String uri = "";
        if (outgoing == null) {
            throw new NotFoundException("Manifest !");
        }
        if (outgoing.getStatus() == TOutGoingEnum.PENDING.getCode() && outgoing.getStatus() != TOutGoingEnum.COURIER_DATA.getCode()) {
            if (courierId == null || isPickupVendor == null || courierName == null || phone == null || codeOutgoing == null)
                throw new NotFoundException("Corier Data Belom Ada !");
            saveCourierOutgoing(courierId, isPickupVendor, courierName, phone, codeOutgoing, userId);
        }
        if (outgoing.getStatus() == TOutGoingEnum.PENDING.getCode() || outgoing.getStatus() == TOutGoingEnum.COURIER_DATA.getCode()) {

            outgoing = outgoingListRepo.findByCode(codeManifest);
            List<TOutgoingListDetailEntity> listDetailEntities = outgoingListDetailRepo.findAllByOutgoingListId(outgoing.getIdOutgoingList());
            for (TOutgoingListDetailEntity entity : listDetailEntities) {
                TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(entity.getBookingCode());
                entity.getBookingCode().setStatus(outgoing.getIsPickupVendor() ?
                        PaymentEnum.PICK_BY_VENDOR.getValue() :
                        PaymentEnum.SEND_TO_VENDOR.getValue());
                this.historyTransactionService.createHistory(entityPaymentHistory, entity.getBookingCode(), userId);
                TLeadTimeEntity leadTime = new TLeadTimeEntity();
                TLeadTimeHistoryEntity leadTimeHistory = new TLeadTimeHistoryEntity();
                leadTime.setBookingCode(entity.getBookingCode());
                leadTime.setTrxDate(entity.getBookingCode().getTrxDate());
                leadTime.setTimeLeave(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                leadTime.setStatus(LeadTimeStatusEnum.PROCESS.getString());
                leadTimeHistory.setBookingCode(entity.getBookingCode().getBookingCode());
                leadTimeHistory.setStt(entity.getBookingCode().getStt());
                leadTimeHistory.setTrxDate(LocalDateTime.now());
                leadTimeHistory.setLastUpdate(LocalDateTime.now());
                leadTimeHistory.setDescription(DESCRIPTION_LEAD_TIME);
                tLeadTimeRepo.save(leadTime);
                tLeadTimeHistoryRepo.save(leadTimeHistory);
            }
            this.outgoingListDetailRepo.saveAll(listDetailEntities);
            outgoing.setStatus(TOutGoingEnum.PRINT.getCode());
            outgoing.setProcessDate(LocalDate.now());
            outgoing.setProcessTime(LocalTime.now());
            outgoingListRepo.save(outgoing);
           
        }
        uri = urlcetak + "warehouse/print?tisCable=" + codeManifest + "&userId=" + userId;
        return SaveResponse.builder()
                .saveInformation("Print Outgoing")
                .saveStatus(1)
                .linkResi(uri)
                .build();
    }

    public BookDataResponse checkItemBarang(String bookingCode) {
        PaymentDto paymentDto = paymentService.findByBookingCodeAndStatusRequest(bookingCode, Arrays.asList(PaymentEnum.REQUEST.getCode()));
        if (paymentDto == null) {
            throw new NotFoundException("Qr Code Tidak Di temukan Atau Belom Di pickup Oleh Kurir Silahkan Cek kembali ");
        }

        return paymentService.toBookDataResponse(paymentDto);
    }

    @Transactional
    public SaveResponse receiveWarehouseBarang(ReceiveWarehouseRequest request) {
        PaymentDto paymentDto = paymentService.findByBookingCodeAndStatusRequest(request.getBookingCode(), Arrays.asList(PaymentEnum.REQUEST.getCode()));
        if (!validateNumberTelp(request.getCode(), paymentDto.getPaymentEntity().getReceiverTelp())) {
            throw new NotFoundException("Inputan Tidak Sesuai, pastikan memasukkan 3 digit terakhir pada nomor telepon penerima !");
        }
        if(depositBookService.checkQrCodeExt(bookCounterService.replaceRegexQrcodeExt(request.getQrCodeExt()))) {
        	throw new NotFoundException("QrCode : "+request.getQrCodeExt() +" sudah digunakan !");
        }
        TPaymentEntity payment = paymentDto.getPaymentEntity();
        TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(payment);
        MUserEntity userAdmin = mUserRepo.getMUserEntitiesBy(request.getUserId());
        payment = bookService.insertWarehouseBarangTitipan(payment, userAdmin, PaymentEnum.REQUEST, request.getOfficeCode());
        payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
        payment.setOfficeCode(request.getOfficeCode());
        payment.setQrcodeExt(bookCounterService.replaceRegexQrcodeExt(request.getQrCodeExt()));
        paymentDto.setPaymentEntity(payment);
        paymentDto.setPaymentEnum(PaymentEnum.RECEIVE_IN_WAREHOUSE);
        THistoryBookEntity historyBook = bookService.initializeHistoryBook(paymentDto.getBookCode(), paymentDto.getPaymentEntity().getJumlahLembar(), request.getUserId(), PaymentEnum.RECEIVE_IN_WAREHOUSE.getKeterangan(), paymentDto.getPaymentEntity().getStt());
        tHistoryBookRepo.save(historyBook);
        this.historyTransactionService.createHistory(entityPaymentHistory, payment, request.getUserId());
        paymentDto = paymentService.savePayment(paymentDto);

        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil Simpan Barang di Warehouse")
                .build();
    }

    @Transactional
    public FooterReceiveWarehouseResponse receiveWarehouseFromCounter(ReceiveWarehouseRequest request) {
        PaymentDto paymentDto = paymentService.findByBookingCodeAndStatusRequest(request.getQrCode(), Arrays.asList(PaymentEnum.OUTGOING_BY_COUNTER.getCode()));
        TPaymentEntity payment = paymentDto.getPaymentEntity();
        TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(payment);
        MUserEntity userAdmin = mUserRepo.getMUserEntitiesBy(request.getUserId());
        TOutgoingCounterDetailEntity outgoing = tOutgoingCounterDetailRepo.findByBookingCode(payment);
        if (outgoing == null) throw new NotFoundException("Pesanan Tidak Ditemukan dalam Outgoing");
        Integer totalOutgoing = tOutgoingCounterDetailRepo.countByOutgoingCounterIdAndStatus(outgoing.getOutgoingCounterId(), TOutGoingCounterDetailEnum.PROCESS.getCode());
        outgoing.setStatus(TOutGoingCounterDetailEnum.RECEIVE.getCode());
        tOutgoingCounterDetailRepo.save(outgoing);
//        if(totalOutgoing == null) {
        outgoing.getOutgoingCounterId().setStatus(TOutGoingCounterEnum.RECEIVE_IN_WAREHOUSE.getCode());
        tOutgoingCounterRepo.save(outgoing.getOutgoingCounterId());
//        }
        payment = bookService.insertWarehouseBarangTitipan(payment, userAdmin, PaymentEnum.RECEIVE_IN_WAREHOUSE, request.getOfficeCode());
        paymentDto.setPaymentEntity(payment);
//        paymentDto.setPaymentEnum(PaymentEnum.ACCEPT_IN_WAREHOUSE);
        paymentDto.setPaymentEnum(PaymentEnum.RECEIVE_IN_WAREHOUSE);
        this.historyTransactionService.createHistory(entityPaymentHistory, payment, request.getUserId());
        paymentDto = paymentService.savePayment(paymentDto);

        return FooterReceiveWarehouseResponse.builder()
                .courierName("")
                .pickupTime("")
                .pesananBelomDiterima(0)
                .barangBelomDiterima(0)
                .build();
    }

    public SaveResponse checkBarangOutgoing(String outgoinNumber) {
        TOutgoingListEntity outgoingList = outgoingListRepo.findByCode(outgoinNumber);
        if (outgoingList == null) throw new NotFoundException("Outgoing Tidak Ditemukan !");
        Integer total = tPaymentRepo.countByStatusAndSwitcherCodeAndOfficeCode(Arrays.asList(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode(), PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode(), PaymentEnum.FINISH_INPUT_AND_PAID.getCode()),
                outgoingList.getSwitcherEntity().getSwitcherCode(), Arrays.asList(outgoingList.getOfficeCode().getOfficeCode()));

        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation(total == null ? "0" : total.toString())
                .build();
    }

    private Boolean validateNumberTelp(String code, String phoneNumber) {
        Boolean result = false;
        String digit = "";
        Integer length = phoneNumber.length();
        digit = phoneNumber.substring(length - 3);
        if (code == null) return false;
        if (code.equals(digit)) result = true;
        return result;
    }

    public TotalBarangInCourierResponse getBarangInCourierByQrCode(String qrCodeExt, String courierUser) {
        List<TPickupDetailEntity> lPickupDtl = null;
        Integer totalBarang = 0 ;
        Integer totalBarangAssign = 0; 
        MUserEntity userEntity = null;
        if (qrCodeExt != null) {
            PaymentDto paymentDto = paymentService.findByqrCodeExtOrBookIdOrSTT(qrCodeExt);
            TPaymentEntity payment = null;
            TPickupOrderRequestDetailEntity orderDetail = null;
            TPickupDetailEntity pickupDetail = null;
            if (paymentDto == null) {
                orderDetail = tPickupOrderRequestDtl.findByQrcodeExt(qrCodeExt);
                if (orderDetail == null) {
                    throw new NotFoundException("Pesanan Tidak Ditemukan !");
                }
                pickupDetail = pickupService.findByPickupOrderReq(orderDetail.getOrderRequestEntity());

            } else {
                payment = paymentDto.getPaymentEntity();
                if (paymentDto.getPaymentEnum() == PaymentEnum.OUTGOING_BY_COUNTER || paymentDto.getPaymentEnum() == PaymentEnum.FINISH_INPUT_AND_PAID) {
                    return TotalBarangInCourierResponse.builder()
                            .userId(payment.getUserId().getUserId())
                            .name(payment.getUserId().getName())
                            .totalBarang(1)
                            .build();
                }
                pickupDetail = pickupService.getPickupDetail(payment.getBookingCode());
            }
            lPickupDtl = pickupService.findByPickupIdAndStatus(pickupDetail.getPickupId(), PickupDetailEnum.IN_COURIER.getValue());
            userEntity = pickupDetail.getPickupId().getCourierId();

        }
        if (courierUser != null) {
            userEntity=mUserRepo.getOne(courierUser);
        }
        lPickupDtl=pickupService.findByCourier(userEntity.getUserId(),Arrays.asList(PickupDetailEnum.IN_COURIER.getValue(),PickupDetailEnum.ASSIGN_PICKUP.getValue()));
        if(lPickupDtl.size() > 0) {
        	for(TPickupDetailEntity pickupDtl : lPickupDtl) {
        		if(pickupDtl.getPickupOrderRequestEntity() != null) {
        			Integer total  = tPickupOrderRequestDtl.countByOrderRequestEntityAndStatus(pickupDtl.getPickupOrderRequestEntity(), RequestPickupEnum.IN_COURIER.getValue());
        			Integer totalAssign = tPickupOrderRequestDtl.countByOrderRequestEntityAndStatus(pickupDtl.getPickupOrderRequestEntity(), RequestPickupEnum.ASSIGN_PICKUP.getValue());
        			totalBarang = totalBarang + total;
        			totalBarangAssign = totalBarangAssign + totalAssign;
        			if(total == 0 && totalAssign == 0) {
        				if(pickupDtl.getPickupOrderRequestEntity().getStatus().equals(RequestPickupEnum.ASSIGN_PICKUP.getValue())) {
        					totalBarangAssign = totalBarangAssign + 1;
        				}else {
        					totalBarang = totalBarang + 1;
        				}
        			}
        			//log.info("Pickup Order Id : "+ pickupDtl.getPickupOrderRequestEntity().getPickupOrderId());
        		}else {
        			if(pickupDtl.getStatus().equals(PickupDetailEnum.ASSIGN_PICKUP.getValue())) {
        				totalBarangAssign = totalBarangAssign + 1;
        			}
        			if(pickupDtl.getStatus().equals(PickupDetailEnum.IN_COURIER.getValue())) {
        				totalBarang = totalBarang + 1;
        			}
        			//log.info("Booking Code : "+ pickupDtl.getBookId().getBookingCode());
        		}
        		//log.info("Total Assign Pikcup : "+totalBarangAssign);
        		//log.info("Total IN Courier : "+totalBarang);
        	}
        }
        return TotalBarangInCourierResponse.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .totalBarang(totalBarang)
                .totalBarangAssign(totalBarangAssign)
                .build();
    }

    public SaveResponse addReasonSkipReceiveBarang(String userCourier, String reason, String userAdmin) {
//        PaymentDto paymentDto = paymentService.findByqrCodeExt(qrCodeExt);
//        TPaymentEntity payment = null;
//        TPickupOrderRequestDetailEntity orderDetail = null;
//        TPickupDetailEntity pickupDetail = null;
//        if (paymentDto.getPaymentEntity() == null) {
//            orderDetail = tPickupOrderRequestDtl.findByQrcodeExt(qrCodeExt);
//            if (orderDetail == null) {
//                throw new NotFoundException("Pesanan Tidak Ditemukan !");
//            }
//            pickupDetail = pickupService.findByPickupOrderReq(orderDetail.getOrderRequestEntity());
//
//        } else {
//            payment = paymentDto.getPaymentEntity();
//            pickupDetail = pickupService.findByCourier(userCourier,);
//        }
    	
        List<TPickupDetailEntity> lPickupDtl = pickupService.findAllByCourier(userCourier, Arrays.asList(PickupDetailEnum.IN_COURIER.getValue()));
        for (TPickupDetailEntity pickupDtl : lPickupDtl) {
            if (pickupDtl.getBookId() != null) {
                TPaymentEntity pay = pickupDtl.getBookId();
                historyTransactionService.createHistory(pay, pay, userAdmin);
                TPaymentHistoryEntity payHistory = historyTransactionService.getPaymentHistory(pay, pay.getStatus(), pay.getStatus());
                payHistory.setReason(reason);
                historyTransactionService.saveHistory(payHistory);
            } else {
                TPickupOrderRequestEntity pickupReq = pickupDtl.getPickupOrderRequestEntity();
                List<TPickupOrderRequestDetailEntity> lPickupReqDtl = tPickupOrderRequestDtl.findAllByOrderRequestEntity(pickupReq);
                for (TPickupOrderRequestDetailEntity reqDtl : lPickupReqDtl) {
                    historyTransactionService.historyRequestPickup(pickupReq, reqDtl, RequestPickupEnum.IN_COURIER.getValue(), userAdmin, reason);
                }
            }
        }
        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil Skip Terima Pesanan")
                .build();
    }
}
