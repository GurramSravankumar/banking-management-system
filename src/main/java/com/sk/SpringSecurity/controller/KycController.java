package com.sk.SpringSecurity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.SpringSecurity.Kyc.KycService;
import com.sk.SpringSecurity.dtos.KycResponseDTO;
import com.sk.SpringSecurity.dtos.KycUploadRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;


    @PostMapping("/upload")
    public ResponseEntity<KycResponseDTO> uploadKyc(
            @ModelAttribute KycUploadRequestDTO request) {


        KycResponseDTO response = kycService.uploadKyc(request);


        if ("KYC uploaded successfully".equals(response.getMessage())) {

            return ResponseEntity
                    .status(HttpStatus.CREATED) 
                    .body(response);
        }


        if ("KYC already submitted".equals(response.getMessage())) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT) 
                    .body(response);
        }


        if ("User not found".equals(response.getMessage())) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) 
                    .body(response);
        }


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) 
                .body(response);
    }

}