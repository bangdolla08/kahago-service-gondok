package com.kahago.kahagoservice.model.request;

import java.util.List;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class VendorGoodsRequest {
	private Integer productSwCode;
	List<GoodsReq> lGoods;
}
