package com.kahago.kahagoservice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.repository.MCouponDiscountRepo;
import com.kahago.kahagoservice.repository.MTutorialRepo;

@Controller
public class BlastController {
    @Autowired
    private MTutorialRepo tutorialRepo;
    @Autowired
    private MCouponDiscountRepo kuponRepo;
    @Autowired
    private MTutorialRepo mTutorialRepo;
    @Value("${kahago.image.tutorial}")
    private String imageTutorial;

    @GetMapping(value = "/blast/{blastType}/{blastId}", produces = MediaType.IMAGE_PNG_VALUE)
    public void getImageBlast(@PathVariable String blastType,
                              @PathVariable String blastId,
                              HttpServletResponse response) throws IOException {
        String pathpic = "";
        if (blastType.equals("Promo")) {
            pathpic = tutorialRepo.findById(Integer.valueOf(blastId.split("[.]")[0])).get().getPathImage();
        } else if (blastType.equals("Coupon")) {
            pathpic = kuponRepo.findById(Integer.valueOf(blastId.split("[.]")[0])).get().getPathBlastImage();
        } else if (blastType.equals("Tutorial")) {
            pathpic = mTutorialRepo.findById(Integer.valueOf(blastId.split("[.]")[0])).get().getPathBlastImage();
        }

        File file = new File(pathpic);
        if (file.exists()) {
            response.setContentType("application/octet-stream");
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            IOUtils.copy(in, out);
            out.close();
            in.close();
        } else {
            throw new NotFoundException("image not found");
        }
    }
}
