package com.kahago.kahagoservice.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "m_coupon_discount")
@Data
public class MCouponDiscountEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String couponCode;
    private String couponName;
    private Integer couponType;
    private Double percentageDiscount;
    private BigDecimal nominalDiscount;
    private Integer maxDiscount;
    private Integer minTransaction;
    private LocalDate expiredStartDate;
    private LocalDate expiredEndDate;
    private Boolean isOneUse;
    private Boolean isDevice;
    private Integer byCategoryUser;
    private Integer byVendor;
    private Integer byProduct;
    private Integer byOptionPayment;
    private String urlFrontImage;
    private String urlBackgroundImage;
    private String description;
    private Boolean isActive;
    private String createdBy;
    private String updateBy;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Boolean isReceiver;
    private Boolean isPublic;
    private boolean showDashboard;
    private Integer referenceCoupon;
    private String pathBlastImage;
    private Boolean isFirstUse;
    private BigDecimal limitDiscount;
}
