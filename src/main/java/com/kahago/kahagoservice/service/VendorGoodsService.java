package com.kahago.kahagoservice.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MGoodsEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.TGoodsEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.GoodsReq;
import com.kahago.kahagoservice.model.request.VendorGoodsRequest;
import com.kahago.kahagoservice.model.response.GoodsRes;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.VendorGoodsResponse;
import com.kahago.kahagoservice.repository.MGoodsRepo;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.TGoodsRepo;
import com.kahago.kahagoservice.util.CommonConstant;

/**
 * @author Ibnu Wasis
 */
@Service
public class VendorGoodsService {
	@Autowired
	private TGoodsRepo tGoodsRepo;
	@Autowired
	private MProductSwitcherRepo mProductSwitcherRepo;
	@Autowired
	private MGoodsRepo mGoodsRepo;
	
	public List<VendorGoodsResponse> getVendor(){
		List<TGoodsEntity> lVendor = tGoodsRepo.findAll();
		List<VendorGoodsResponse> result = new ArrayList<VendorGoodsResponse>();
		for(TGoodsEntity ge : lVendor) {
			VendorGoodsResponse res = VendorGoodsResponse.builder()
									.switcherCode(ge.getProductSwCode().getSwitcherEntity().getSwitcherCode())
									.switcherName(ge.getProductSwCode().getSwitcherEntity().getName())
									.build();
			if(result.isEmpty()) {
				result.add(res);
			}
			if(!result.contains(res)) {
				result.add(res);
			}
		}
		Comparator<VendorGoodsResponse> sort = (a,b)->a.getSwitcherCode().compareTo(b.getSwitcherCode());
		Collections.sort(result, sort);
		return result;
	}
	
	public List<VendorGoodsResponse> getAllGoodsByProduct(Integer productSwCode,Integer switcherCode){
		List<TGoodsEntity> lvendorGoods = tGoodsRepo.findAllByswitcherCode(productSwCode, switcherCode);
		List<VendorGoodsResponse> response = new ArrayList<>();
		for(TGoodsEntity ge : lvendorGoods) {
			VendorGoodsResponse gr = toDto(ge);
			if(response.isEmpty()) {
				response.add(gr);
			}
			if(!response.contains(gr)) {
				response.add(gr);
			}
		}
		return response;
	}
	
	private VendorGoodsResponse toDto(TGoodsEntity entity) {
		List<GoodsRes> detailGoods = getAllGoodsByProduct(entity.getProductSwCode());
		
		return VendorGoodsResponse.builder()
				.switcherCode(entity.getProductSwCode().getSwitcherEntity().getSwitcherCode())
				.switcherName(entity.getProductSwCode().getSwitcherEntity().getName())
				.productSwCode(entity.getProductSwCode().getProductSwCode().intValue())
				.productName(entity.getProductSwCode().getName())
				.detailGoods(detailGoods)
				.build();
	}
	
	private List<GoodsRes> getAllGoodsByProduct(MProductSwitcherEntity product){
		List<TGoodsEntity> lGoods = tGoodsRepo.findAllByProductSwCode(product);
		List<GoodsRes> lGoodRes = new ArrayList<>();
		for(TGoodsEntity ge : lGoods) {
			GoodsRes resp = GoodsRes.builder()
						.id(ge.getGoodsId().getGoodsId().toString())
						.goodsName(ge.getGoodsId().getGoodsName())
						.status(CommonConstant.toBoolean(ge.getStatus()==null?0:ge.getStatus()))
						.packFlag(CommonConstant.toBoolean(ge.getFlagSurcharge()==null?0:ge.getFlagSurcharge().intValue()))
						.build();
			lGoodRes.add(resp);
		}
		Comparator<GoodsRes> sort = (a,b)->Integer.valueOf(a.getId()).compareTo(Integer.valueOf(b.getId()));
		Collections.sort(lGoodRes, sort);
		return lGoodRes;
	}
	@Transactional(rollbackOn=Exception.class)
	public SaveResponse saveVendorGoods(VendorGoodsRequest request) {
	    	MProductSwitcherEntity product = mProductSwitcherRepo.findByProductSwCode(request.getProductSwCode().longValue());
	    	if(product==null) {
	    		throw new NotFoundException("Data Tidak Ditemukan !");
	    	}
	    	for(GoodsReq req : request.getLGoods()) {
	    		TGoodsEntity goods = tGoodsRepo.findAllByGoodsIdAndProductSwCode(req.getGoodsId().longValue(), product);
	    		MGoodsEntity good = mGoodsRepo.findByGoodsId(req.getGoodsId().longValue());
	    		String status = "0";
	    		String flagPck ="0";
	    		if(req.getStatus()) {
	    			status = "1";
	    		}
	    		if(good.getPackFlag()) {
	    			flagPck="1";
	    		}
	    		if(goods != null) {
	    			goods.setStatus(Byte.valueOf(status));
	    			goods.setAddinformation(req.getInformation()==null?"":req.getInformation());
	    			goods.setUpdateBy("admin");
	    			goods.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
	    		}else {
	    			goods = new TGoodsEntity();
	    			goods.setProductSwCode(product);
	    			goods.setGoodsId(good);
	    			goods.setFlagSurcharge(Byte.valueOf(flagPck));
	    			goods.setAddinformation(req.getInformation()==null?"":req.getInformation());
	    			goods.setCreatedBy("admin");
	    			goods.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
	    			goods.setIsEditLiability(Byte.valueOf("0"));
	    			goods.setLiabilityValue(0);
	    			goods.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
	    			goods.setUpdateBy("admin");
	    			goods.setStatus(Byte.valueOf(status));
	    		}
	    		tGoodsRepo.save(goods);
	    	}
	    	
	    	return SaveResponse.builder()
	    			.saveInformation("Berhasil Simpan Komoditas Vendor")
	    			.saveStatus(1)
	    			.build();
	    }
}
