package com.kahago.kahagoservice.model.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class BookRequestPCP {
	@NotEmpty
	private String destinationId;
	@NotNull
	private Integer destinationCityId;
	@NotNull
    private Integer destinationProvinceId;
	@NotEmpty
	private String originId;
	@NotNull
	private Integer serviceId;
	@NotNull
	private Integer shipTypeId;
	@NotNull
	private Double totalActualWeight;
	@NotNull
	private Integer contentId;
	@NotNull
	private Integer goodsId;
	@NotNull
	private Integer handlingId;
	@NotNull
	private String shipperName;
    @NotEmpty
    private String shipperAddress;
    @NotEmpty
    private String shipperTelephone;
    @NotEmpty
    private String shipperEmail; //No required
    @NotEmpty
    private String shipperZipCode; //No required

    @NotEmpty
    private String receiverName;
    @NotEmpty
    private String receiverAddress;
    @NotEmpty
    private String receiverTelephone;
    @NotEmpty
    private String receiverEmail; //No required
    @NotEmpty
    private String receiverZipCode; //No required

    @NotNull
    private Boolean isInsurance;
    @NotNull
    private Boolean isPacking;
    @NotNull
    private Double valueOfGoods;
    @NotNull
    private Double valueOfInsurance;
    @NotEmpty
    private String notes;
    @Valid @NotEmpty
    private List<ItemPCP> items;
	
}
