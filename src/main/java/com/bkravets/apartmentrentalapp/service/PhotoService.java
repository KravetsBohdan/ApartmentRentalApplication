package com.bkravets.apartmentrentalapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {
    String uploadImage(MultipartFile file);
}
