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
public class TransactionReceiptResponseDTO {
	
	private String referenceNumber;
	
	private Long  amount;
	
	private String status;
	
	private String transactionType;
	
	private String transactionMode;
	
	private String fromAccountNumber;
	
	private String fromAccountHolderName;
	
	private String toAccountNumber;
	
	private String toAccountHolderName;
	
	private Long balanceBefore;
	
	private Long balanceAfter;
	
	private String remarks;
	
	private LocalDateTime transactionTime;

}
