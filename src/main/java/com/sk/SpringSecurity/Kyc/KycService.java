package com.sk.SpringSecurity.Kyc;

import com.sk.SpringSecurity.dtos.KycResponseDTO;
import com.sk.SpringSecurity.dtos.KycUploadRequestDTO;

public interface KycService {

    KycResponseDTO uploadKyc(KycUploadRequestDTO request);

}