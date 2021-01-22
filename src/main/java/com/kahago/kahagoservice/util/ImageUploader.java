package com.kahago.kahagoservice.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class ImageUploader implements Uploader {

    private MultipartFile file;
    private String filenameOfImage;
    private File target;
    private boolean isOriginalName;

    public ImageUploader(File target, String filenameOfImage, MultipartFile file) {
        this.file = file;
        this.filenameOfImage = filenameOfImage;
        this.target = target;
    }

    private boolean isSupportContentType(String contentType) {
        return contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg");
    }
}
