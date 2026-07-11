package com.sk.SpringSecurity.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBeneficiaryRequestDTO {

    private String beneficiaryName;

    private String nickname;

    private String accountNumber;

    private String ifscCode;

    private String bankName;

}
