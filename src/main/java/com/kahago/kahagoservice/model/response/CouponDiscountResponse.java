package com.kahago.kahagoservice.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.kahago.kahagoservice.model.request.UserCategory;
import lombok.Builder;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@Builder
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
public class CouponDiscountResponse {
	private Integer idCoupon;
	private String couponCode;
	private String couponName;
	private Double percetageDiscont;
	private BigDecimal percetageNominal;
	private Integer maxDiscount;
	private Integer minTransaction;
	private LocalDate startDate;
	private LocalDate endDate;
	private Boolean inOneUse;
	private Boolean isDevice;
	private Boolean isUserCategory;
	private Boolean isFirstUse;
	private Boolean isVendor;
	private Boolean isProductSw;
	private Boolean isOptionPayment;
	private String urlFrontImage;
	private String urlBackImage;
	private String desc;
	private Boolean isPublic;
	private Boolean isActive;
	private String urlBlastImage;
	private Integer referenceCode;
	private Boolean isDashboard;
	private Boolean isReceiver;
	private Boolean couponType;
	private BigDecimal limitDiscount;
	private List<UserCategoryResponse> userCategory;
	private List<VendorResponse> listVendor;
	private List<ProductResponse> listProduct;
	private List<OptionPaymentResponse> listPayment;
}
