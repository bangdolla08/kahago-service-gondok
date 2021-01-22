package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.VendorArea;
import io.swagger.annotations.OAuth2Definition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PriceVendorServiceTest {
    @Autowired
    private PriceVendorService priceVendorService;
    @Test
    void createSaveNewProvince() {
        VendorArea vendorArea=
                VendorArea
                        .builder()
                        .province("Enggok Enggokan")
                        .build();
//        SaveResponse saveResponse=priceVendorService.saveArea(vendorArea,"Test Data");
//        System.out.print(saveResponse.getSaveInformation());
//        assertEquals(saveResponse.getSaveStatus(),1);
    }
    @Test
    void editProvince(){
//        VendorArea vendorArea=
//                VendorArea
//                        .builder()
//                        .provinceId(35)
//                        .province("Edit Ahhh")
//                        .build();
//        SaveResponse saveResponse=priceVendorService.saveArea(vendorArea,"Test Data");
//        System.out.print(saveResponse.getSaveInformation());
//        assertEquals(saveResponse.getSaveStatus(),1);
    }
}