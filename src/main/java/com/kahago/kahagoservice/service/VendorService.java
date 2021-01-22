package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.Product;
import com.kahago.kahagoservice.model.response.SttMonitorRes;
import com.kahago.kahagoservice.model.response.VendorResponse;
import com.kahago.kahagoservice.repository.MSwitcherRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kahago.kahagoservice.repository.TSttVendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.configuration.CommonConfig;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;

import static com.kahago.kahagoservice.util.ImageConstant.*;

@Service
public class VendorService {
    @Autowired
    private MProductSwitcherRepo productSwitcherRepo;
    @Autowired
    private MSwitcherRepo switcherRepo;


    public MProductSwitcherEntity getProductSwitcher(String pswcode) {
        return productSwitcherRepo.getOne(Long.valueOf(pswcode));
    }

    public MSwitcherEntity getSwitcherEntity(Integer idSwitcher) {
        return switcherRepo.findById(idSwitcher).orElseThrow(() -> new NotFoundException("Vendor Tidak Ditemukan !"));
    }

    public List<VendorResponse> getAll(Boolean isAll) {
//		List<Long> listcodeProduct = productSwitcherRepo.getMaxSwitcherCode(Arrays.asList((byte)0,(byte)1));
        List<MProductSwitcherEntity> lAllActiveProduct =
                (isAll) ? productSwitcherRepo.getByProductSWAndSwitcherCode(true)
                        : productSwitcherRepo.getByProductSWAndSwitcherCodeAndActive(true, Byte.parseByte("0"));
        List<VendorResponse> vendors = new ArrayList<>();
        for (MProductSwitcherEntity et : lAllActiveProduct) {
            List<Product> products = new ArrayList<>();
            VendorResponse vendor = new VendorResponse();
            for (MProductSwitcherEntity ex : lAllActiveProduct) {
                Product product = new Product();
                if (ex.getSwitcherEntity().getSwitcherCode().equals(et.getSwitcherEntity().getSwitcherCode())) {
                    product.setProductDisplayName(ex.getDisplayName());
                    product.setProductCode(ex.getProductSwCode());
                    products.add(product);
                }
            }

            vendor.setDisplayName(et.getSwitcherEntity().getDisplayName());
            vendor.setName(et.getSwitcherEntity().getName());
            vendor.setProduct(products);
            vendor.setImages(PREFIX_PATH_IMAGE_VENDOR + et.getSwitcherEntity().getImg().substring(et.getSwitcherEntity().getImg().lastIndexOf("/") + 1));
            vendor.setSwicherCode(et.getSwitcherEntity().getSwitcherCode());

            if (!vendors.contains(vendor)) {
                vendors.add(vendor);
            } else {
                continue;
            }

        }
        return vendors;
    }

    public List<VendorResponse> getAllProduct() {
        List<MProductSwitcherEntity> lAllActiveProduct = productSwitcherRepo.getAllProductActive();
        List<VendorResponse> vendors = new ArrayList<>();
        for (MProductSwitcherEntity product : lAllActiveProduct) {
            VendorResponse vendor = new VendorResponse();
            Product prd = new Product();
            List<Product> products = new ArrayList<>();
            prd.setProductCode(product.getProductSwCode());
            prd.setProductDisplayName(product.getDisplayName());
            products.add(prd);
            vendor.setDisplayName(product.getSwitcherEntity().getDisplayName());
            vendor.setName(product.getSwitcherEntity().getName());
            vendor.setProduct(products);
            vendor.setImages(PREFIX_PATH_IMAGE_VENDOR + product.getSwitcherEntity().getImg().substring(product.getSwitcherEntity().getImg().lastIndexOf("/") + 1));
            vendor.setSwicherCode(product.getSwitcherEntity().getSwitcherCode());
            vendors.add(vendor);
        }

        return vendors;
    }
}
