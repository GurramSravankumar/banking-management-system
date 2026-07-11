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
public class TransactionResponseDTO {

    private String referenceNumber;

    private String transactionTitle;

    private String beneficiaryNickname;

    private String transactionType;

    private Long amount;

    private String status;

    private String remarks;

    private LocalDateTime transactionTime;

    private String fromAccountNumber;

    private String toAccountNumber;

}
