package com.kahago.kahagoservice.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
@JsonSerialize
public class DatasItem {
	@JsonProperty("cust_package_id")
    private String custPackageId;

	@JsonProperty("receipt_number")
	private String receiptNumber;

	public void setReceiptNumber(String receiptNumber){
		this.receiptNumber = receiptNumber;
	}

	public String getReceiptNumber(){
		return receiptNumber;
	}

	public void setCustPackageId(String custPackageId){
		this.custPackageId = custPackageId;
	}

	public String getCustPackageId(){
		return custPackageId;
	}

    @Override
    public String toString() {
        return "DatasItem{" +
                "custPackageId='" + custPackageId + '\'' +
                ", receiptNumber='" + receiptNumber + '\'' +
                '}';
    }
}
