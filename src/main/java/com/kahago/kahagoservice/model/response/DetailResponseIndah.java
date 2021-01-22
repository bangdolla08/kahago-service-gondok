package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
@JsonInclude(value=Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DetailResponseIndah {
	private String idTrans;
	private String noInvoice;
	private String idLayanan;
	private String idKecamatanAsal;
	private String idKecamatanTujuan;
	private String noResi;
	private String berat;
	private String satuan;
	private String tarif;
	private String estimasi;
	private String namaPengirim;
	private String noHpPengirim;
	private String alamatPengirim;
	private String namaPenerima;
	private String noHpPenerima;
	private String alamatPenerima;
	private String dateCreated;
	private String dateUpdated;
	private String status;
	private String nmPaket;
	private String layanan;
	private String namaKecamatanAsal;
	private String namaKecamatanTujuan;
	private String idKotaAsal;
	private String idKotaTujuan;
	private String idTarif;
	private String leadTime;
	private String namaKotaAsal;
	private String namaKotaTujuan;
}
