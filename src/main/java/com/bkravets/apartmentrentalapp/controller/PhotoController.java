package com.bkravets.apartmentrentalapp.controller;

import com.bkravets.apartmentrentalapp.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping(value = "/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        return photoService.uploadImage(file);
    }
}
