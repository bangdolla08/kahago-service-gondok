package com.kahago.kahagoservice.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class CouponDiscountRequest {
	private Integer idCoupon;
	@NotEmpty
	private String CouponName;
	private Boolean GenerateKuponAuto;
	private String couponCode;
	private Boolean couponType;
	private BigDecimal nominal;
	private Double percetage;
	private Boolean isMaxDiscount;
	private Integer maxDiscount;
	private Boolean isMinTrx;
	private Integer minimumTrx;
	@NotEmpty
	private String startDate;
	@NotEmpty
	private String endDate;
	private Boolean isOneUse;
	private Boolean isDevice;
	private Boolean isUserCategory;
	private List<UserCategory> lUserCategory;
	private Boolean isVendor;
	private List<Vendor> lVendor;
	private Boolean isProduct;
	private List<ProductSw> lProduct;
	private Boolean isOptionPayment;
	private List<OptionPayment> optionPayment;
	@NotEmpty
	private String desc;
	private Boolean isActive;
	private Boolean isDashboard;
	private Boolean isPublic;
	private Boolean isReceiver;
	private Boolean isFirstUse;
	private String userAdmin;
	private Integer TotalCoupon;
	private ImageRequest frontImage;
	private ImageRequest backImage;
	private ImageRequest blastImage;
	private BigDecimal limitDiscount;
}
