package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.OutgoingRequest;
import com.kahago.kahagoservice.model.request.ReceiveWarehouseRequest;
import com.kahago.kahagoservice.model.request.SaveCourierReq;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.service.WarehouseService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bangd ON 28/11/2019
 * @project com.kahago.kahagoservice.controller
 */
@BaseController
@ResponseBody
@Validated
public class WarehouseController extends Controller {
    @Autowired
    private WarehouseService warehouseService;

    private static final Logger log = LoggerFactory.getLogger(WarehouseController.class);

    @GetMapping("/warehouse/listbarangdatang")
    @ApiOperation(value = "View a list Barang Datang yang masih dalam setatus pickup by courier")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<BookDataResponse>> find(ReceiveWarehouseRequest request, HttpServletRequest req) {
        log.info("==> List Barang Datang <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        Page<BookDataResponse> bookDataResponses = warehouseService.listBookInCourier(request, request.getPageRequest());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(bookDataResponses),
                bookDataResponses.getContent()
        );
    }

    @GetMapping("/warehouse/listbarangdatang/scanBarangDatang")
    @ApiOperation(value = "Check Barang Datang Apakah Sudah Bisa Di proses Atau Belom")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<BookDataResponse> scanBarangDatang(ReceiveWarehouseRequest request, HttpServletRequest req) {
        log.info("==> Scan Barang <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.checkItem(request));
    }


    @PostMapping("/warehouse/listbarangdatang/scanBarangDatang")
    @ApiOperation(value = "Untuk Proses Barang dimasukkan Dan merubah menjadi Receive In warehouse")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<FooterReceiveWarehouseResponse> receiveWareHouse(@RequestBody ReceiveWarehouseRequest request, HttpServletRequest req) {
        log.info("==> Scan Barang Datang <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.receiveWarehouse(request));
    }

    @GetMapping("/warehouse/outgoing")
    @ApiOperation(value = "Untuk Proses Barang Keluar untuk manifest list nya yang ada")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<OutgoingRequest, List<OutgoingResponse>> getListOutgoing(OutgoingRequest request, HttpServletRequest req) {
        log.info("==> Outgoing barang keluar <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        Page<OutgoingResponse> responses = warehouseService.getOutgoingList(request.getPageRequest(), request);
        return new ResponseWithRequest<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                request,
                responses.getContent(),
                extraPaging(responses));
    }

    @PutMapping("/warehouse/outgoing")
    @ApiOperation(value = "untuk melihat detail manifest list nya yang ada untuk create harap kosongin manifest id dan di isi vendor code nya untuk menampilkan hanya isikan manifest nya ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<OutgoingRequest, OutgoingResponse> getDetailOutgoing(@RequestBody OutgoingRequest request, HttpServletRequest req) {
        log.info("==> Detail manifest list <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new ResponseWithRequest<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                request,
                warehouseService.createOutgoing(request));
    }

    @GetMapping("/warehouse/outgoing/scan/{outGoingManifest}")
    @ApiOperation(value = "Untuk mendapatkan Thumbnail Saat Scan")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String, OutgoingResponse> scanThumbnailOutgoing(@PathVariable String outGoingManifest, HttpServletRequest req) {
        log.info("==> Scan outgoing manifest <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new ResponseWithRequest<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                outGoingManifest,
                warehouseService.scanThumbnail(outGoingManifest));
    }

    @PutMapping("/warehouse/outgoing/scan")
    @ApiOperation(value = "Chack Apakah Bolleh Save Ato Tidak")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<OutgoingRequest, BookDataResponse> scanOutgoing(@RequestBody OutgoingRequest request, HttpServletRequest req) {
        log.info("==> Scan Data Barang <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new ResponseWithRequest<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                request,
                warehouseService.checkBookRequest(request));
    }

    @PostMapping("/warehouse/outgoing/scan")
    @ApiOperation(value = "Save Barang Nya ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<OutgoingRequest, BookDataResponse> saveScanOutgoing(@RequestBody OutgoingRequest request, HttpServletRequest req) {
        log.info("==> Scann Data Barang Detail yang akan keluar <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new ResponseWithRequest<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                request,
                warehouseService.saveBookRequest(request));
    }

    @GetMapping("/warehouse/unfinishbook")
    @ApiOperation(value = "untuk mendapatkan data list yang belom di bayar")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> getListUnfinish(HttpServletRequest req) {
        log.info("==> data list yang belom di bayar <==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new Response<>(null, null, null);
    }

    @PostMapping("/warehouse/savecourier")
    @ApiOperation(value = "Save Courier in Outgoing list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> saveCourier(@RequestBody SaveCourierReq reqData,
                                        HttpServletRequest req
    ) {
        log.info("==> Save Courier Outgoing<==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return warehouseService.saveCourierOutgoing(reqData.getCourierId(), reqData.getIsPickup(), reqData.getCourierName(), reqData.getPhone(), reqData.getOutgoingCode(), reqData.getUserId());
    }

    @PostMapping("/warehouse/upload")
    @ApiOperation(value = "Upload Document outgoing")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> uploadDoc(@RequestParam(value = "file") MultipartFile file,
                                      @RequestParam(value = "outgoingCode") String outgoingCode,
                                      @RequestParam(name = "date", defaultValue = "yyyy-MM-dd") String date,
                                      @RequestParam(name = "time", defaultValue = "HH:mm") String time,
                                      @RequestParam(value = "userId") String userId,
                                      HttpServletRequest req) {
        log.info("==> Upload Outgoing<==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return warehouseService.uploadOutgoing(file, date, time, outgoingCode, userId);
    }

    @PostMapping("/warehouse/cetak")
    @ApiOperation(value = "Print Document outgoing")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> printOutgoing(@RequestBody SaveCourierReq reqData) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.doCetakOutgoing(reqData.getCourierId(), reqData.getIsPickup(), reqData.getCourierName(), reqData.getPhone(), reqData.getOutgoingCode(), reqData.getOutgoingCode(), reqData.getUserId())
        );
    }

    @GetMapping("/warehouse/deleteBookFromOutgoing")
    @ApiOperation(value = "Delete Book From Outgoing ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> deleteOutgoing(@RequestParam(value = "userId") String userId,
                                                 @RequestParam(value = "manifestId") String manifest,
                                                 @RequestParam(value = "bookingCode") String bookingCode) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.doDeleteBookFromManifest(userId, manifest, bookingCode)
        );
    }

    @GetMapping("/warehouse/scan/titipan/{bookingCode}")
    @ApiOperation(value = "Scan Barang Titipan")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<BookDataResponse> scanBarangTitipan(@PathVariable("bookingCode") String bookingCode, HttpServletRequest req) {
        log.info("==> Scan Barang Titipan<==");
        log.info("device ==>" + req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.checkItemBarang(bookingCode));
    }

    @PostMapping("/warehouse/receive/titipan")
    @ApiOperation(value = "Receive Barang titipan ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> receiveBarangTitipan(@RequestBody ReceiveWarehouseRequest request) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.receiveWarehouseBarang(request)
        );
    }

    @GetMapping("/warehouse/outgoing/check")
    @ApiOperation(value = "check barang yang belum masuk outgoing ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> confirmOutgoing(@RequestParam("outgoingNumber") String code) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.checkBarangOutgoing(code)
        );
    }

    @GetMapping("/warehouse/incourier/check")
    @ApiOperation(value = "check barang yang masih di kurir ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<TotalBarangInCourierResponse> getTotalBarangInCourier(@RequestParam(required = false) String qrCodeExt, @RequestParam(required = false) String courierName) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.getBarangInCourierByQrCode(qrCodeExt, courierName)
        );
    }

    @PostMapping("warehouse/skip")
    @ApiOperation(value = "Skip Terima Pesanan ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> skipReceiverBooking(@RequestParam(value = "courier_id") String courierId,
                                                      @RequestParam() String reason,
                                                      Principal principal) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                warehouseService.addReasonSkipReceiveBarang(courierId, reason, principal.getName())
        );
    }
}
