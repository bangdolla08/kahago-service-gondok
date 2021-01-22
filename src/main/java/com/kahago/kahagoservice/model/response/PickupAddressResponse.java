package com.kahago.kahagoservice.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PickupAddressResponse{
	private String userId;
	private String address;
	private Integer idPostalCode;
	private Integer pickupAddressId;
	private String postalCode;
	private String kelurahan;
	private String kecamatan;
	private String kota;
	private String provinsi;
	private String description;
	private String longitude;
	private String latitude;
	private String distance;
	private String phoneNumber;
	private String name;
	private String origin;
}
