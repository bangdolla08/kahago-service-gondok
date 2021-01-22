package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.service.VendorService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.kahago.kahagoservice.util.ImageConstant.*;

/**
 * @author Hendro yuwono
 */
@Controller
public class ImagesController {

    @Value("${kahago.image.bank}")
    private String imageBank;

    @Value("${kahago.image.coupon}")
    private String imageCoupon;
    
    @Value("${kahago.image.tutorial}")
    private String imageTutorial;
    
    @Value("${kahago.image.vendor}")
    private String imageVendor;
    
    @Value("${kahago.image.pickup}")
    private String imagePickup;

    @Value("${kahago.image.optionPayment}")
    private String imageOptionPayment;

    @Autowired
    private VendorService vendorService;
    @GetMapping(value = PREFIX_PATH_IMAGE_BANK + "{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public void imageBank(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = new File(imageBank + name);
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
    @GetMapping(value = PREFIX_PATH_IMAGE_PAYMENT_OPTION + "{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public void imageOptionPayment(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = new File(imageOptionPayment + name);
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
    

    @GetMapping(value = PREFIX_PATH_IMAGE_COUPON + "{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public void imageCoupon(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = new File(imageCoupon + name);
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
    
    @GetMapping(value = PREFIX_PATH_IMAGE_TUTORIAL + "{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public void imageTutorial(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = new File(imageTutorial + name);
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
    
    @GetMapping(value = PREFIX_PATH_IMAGE_VENDOR + "{name}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public void imageVendor(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = new File(imageVendor + name);
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
    
    @GetMapping(value = PREFIX_PATH_IMAGE_PICKUP + "{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public void imagePickup(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = new File(imagePickup + name);
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
