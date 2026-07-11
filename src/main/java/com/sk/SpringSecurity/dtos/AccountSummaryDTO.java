package com.sk.SpringSecurity.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryDTO {

    private Long accountId;

    private Long id;

    private String accountType;

    private String accountNumber;

    private Long balance;

    private String accountStatus;

    private String branchName;

    private String ifscCode;

    private String currency;

}