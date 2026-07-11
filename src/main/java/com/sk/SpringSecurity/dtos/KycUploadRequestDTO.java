package com.sk.SpringSecurity.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycUploadRequestDTO {

    private MultipartFile aadhaarDocument;

    private MultipartFile panDocument;

    private MultipartFile profilePhoto;

}

