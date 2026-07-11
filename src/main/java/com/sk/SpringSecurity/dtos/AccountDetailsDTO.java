package com.sk.SpringSecurity.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailsDTO {

    private Long accountId;
    private String accountNumber;
    private String accountType;
    private String accountStatus;
    private Long balance;
    private String currency;
    private String ifscCode;
    private String branchName;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
