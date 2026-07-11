package com.sk.SpringSecurity.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyRequestDTO {
	
	private Long accountId;

    private Long beneficiaryId;

    private Long amount;

    private String remarks;

    private String requestId;

}