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
public class MiniStatementResponseDTO {

    private String transactionReferenceNumber;

    private String transactionType;   
    
    private Long amount;

    private Long balanceAfter;

    private String remarks;

    private LocalDateTime transactionTime;

}