package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MReceiverEntity;
import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import com.kahago.kahagoservice.model.request.AddressListRequest;
import com.kahago.kahagoservice.model.response.AddressResponse;
import com.kahago.kahagoservice.repository.MReceiverRepo;
import com.kahago.kahagoservice.repository.TPickupAddressRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MSenderEntity;
import com.kahago.kahagoservice.repository.MSenderRepo;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
	@Autowired
	private MSenderRepo senderRepo;
	@Autowired
	private MReceiverRepo receiverRepo;
	@Autowired
	private TPickupAddressRepo pickupAddressRepo;
	@Autowired
	private AreaService areaService;
	@Autowired
	private UserService userService;

	public AddressResponse saveSenderAddress(MSenderEntity entity) {
		return getResponseSender(senderRepo.save(entity));
	}

	public AddressResponse saveReceiverAddress(MReceiverEntity mReceiverEntity){
		return getResponseReceiver(receiverRepo.save(mReceiverEntity));
	}

	public List<AddressResponse> getSenderAddress(AddressListRequest addressListRequest){
		return senderRepo.findAllByUserIdUserId(addressListRequest.getUserId()).stream().map(this::getResponseSender).collect(Collectors.toList());
	}

	public List<AddressResponse> getReceiverAddress(AddressListRequest addressListRequest){
		return receiverRepo.findAllByUserIdAndAreaId(addressListRequest.getUserId(),addressListRequest.getDestinationId())
				.stream().map(this::getResponseReceiver).collect(Collectors.toList());
	}

	private AddressResponse getResponseSender(MSenderEntity mSenderEntity){
		return AddressResponse.builder()
				.id(mSenderEntity.getSenderId())
				.userId(mSenderEntity.getUserId().getUserId())
				.address(mSenderEntity.getSenderAddress())
				.email(mSenderEntity.getSenderEmail()==null?"":mSenderEntity.getSenderEmail())
				.telp(mSenderEntity.getSenderTelp())
				.name(mSenderEntity.getSenderName()).build();
	}

	private AddressResponse getResponseReceiver(MReceiverEntity mReceiverEntity){
		return AddressResponse.builder()
				.name(mReceiverEntity.getReceiverName())
				.address(mReceiverEntity.getReceiverAddress())
				.telp(mReceiverEntity.getReceiverTelp())
				.email(mReceiverEntity.getReceiverEmail()==null?"":mReceiverEntity.getReceiverEmail())
				.id(mReceiverEntity.getReceiverId())
				.postalId(mReceiverEntity.getIdPostalCode().getIdPostalCode())
				.areaId(mReceiverEntity.getIdPostalCode().getKecamatanEntity().getAreaDetailId())
				.postalCode(mReceiverEntity.getIdPostalCode().getPostalCode())
				.kelurahan(mReceiverEntity.getIdPostalCode().getKelurahan())
				.kecamatan(mReceiverEntity.getIdPostalCode().getKecamatanEntity().getKecamatan())
				.kota(mReceiverEntity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.provinsi(mReceiverEntity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
				.userId(mReceiverEntity.getUserId().getUserId())
				.build();
	}


	public TPickupAddressEntity getTPickupAddressEntity(Integer idPickup){
		return this.pickupAddressRepo.getOne(idPickup);
	}

	public MReceiverEntity getReceiver(Integer idReceiver){
		if(idReceiver==null)
			return new MReceiverEntity();
		MReceiverEntity mReceiverEntity=receiverRepo.getOne(idReceiver);
		if(mReceiverEntity==null)
			return new MReceiverEntity();
		return mReceiverEntity;
	}

	public MSenderEntity getSender(Integer idSender){
		if(idSender==null)
			return new MSenderEntity();
		MSenderEntity mReceiverEntity=senderRepo.getOne(idSender);
		if(mReceiverEntity==null)
			return new MSenderEntity();
		return mReceiverEntity;
	}

}
