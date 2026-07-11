package com.sk.SpringSecurity.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycResponseDTO {

    private String message;

    private String kycStatus;

    private boolean aadhaarUploaded;

    private boolean panUploaded;

    private boolean profilePhotoUploaded;

}
