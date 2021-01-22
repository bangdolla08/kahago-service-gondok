package com.kahago.kahagoservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.enummodel.ModulEnum;
import com.kahago.kahagoservice.model.request.RequestBook;
import com.kahago.kahagoservice.model.response.BookDataResponse;

@Service
public class GlobalListService {

	@Autowired
	private ApprovalBookingService approvalBookingService;
	@Autowired
	private WarehouseVerificationService verificationService;
	@Autowired
	private OfficeCodeService officeService;
	@Autowired
	private WarehouseService warehouseService;
	
	public Page<BookDataResponse> getBookingList(RequestBook request){
		Page<BookDataResponse> pagingList = null;
		Boolean isOffice = true;

		List<String> lsOffice = null;
		List<String> lorigin = null;
		if(request.getOfficeCode()!=null) {
			lsOffice=  officeService.getBranchList(request.getOfficeCode().stream().findAny().get()).stream().map(o->o.getOfficeCode()).collect(Collectors.toList());
		}
		if(request.getOfficeCode()!=null) {
			lorigin=  officeService.getBranchList(request.getOfficeCode().stream().findAny().get()).stream().map(o->o.getCity()).collect(Collectors.toList());
		}
		request.setOfficeCode(lsOffice);
		switch (Optional.ofNullable(ModulEnum.getPaymentEnum(request.getModul())).orElse(ModulEnum.BOOK_LIST)) {
		case BOOK_LIST:
				pagingList = approvalBookingService.getListAppBookingByStatusIn(request.getPageRequest(),
						request.getUserId(),request.getStatus(),request.getQrCode(),request.getFilter(),
						lorigin,request.getVendorCode(),request.getOrigin());
			break;
		case VERIFIKASI_BARANG:
			
			pagingList = verificationService.getAllIN(request);
			break;
		case BARANG_DATANG:
			pagingList = warehouseService.listBookInCourierIn(request, request.getPageRequest());
			break;
		case BARANG_TELAT:
//			request.setStatus(Arrays.asList(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode(),PaymentEnum.PICKUP_BY_KURIR.getCode()));
			pagingList = approvalBookingService.getListAppBookingByStatusInLate(request.getPageRequest(),
					request.getUserId(),request.getStatus(),request.getQrCode(),request.getFilter(),
					lsOffice,request.getVendorCode());
			break;
		default:
				pagingList = approvalBookingService.getListAppBookingByStatusIn(request.getPageRequest(),
						request.getUserId(),request.getStatus(),request.getQrCode(),request.getFilter(),
						lsOffice,request.getVendorCode(),request.getOrigin());
			break;
		}
		return pagingList;
	}

}
