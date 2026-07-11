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
public class LoanResponseDTO {
    private Long loanId;
    private Double amount;
    private Double interestRate;
    private Integer termMonths;
    private Double emiAmount;
    private Double outstandingBalance;
    private String loanType;
    private String status;
    private String applicantName;
    private String applicantEmail;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}
