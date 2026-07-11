package com.sk.SpringSecurity.service;

import java.util.List;

import com.sk.SpringSecurity.dtos.AddBeneficiaryRequestDTO;
import com.sk.SpringSecurity.dtos.AccountDetailsDTO;
import com.sk.SpringSecurity.dtos.AccountSummaryDTO;
import com.sk.SpringSecurity.dtos.BeneficiaryResponseDTO;
import com.sk.SpringSecurity.dtos.MiniStatementResponseDTO;
import com.sk.SpringSecurity.dtos.OpenAccountRequestDTO;
import com.sk.SpringSecurity.dtos.TransactionReceiptResponseDTO;
import com.sk.SpringSecurity.dtos.TransactionResponseDTO;
import com.sk.SpringSecurity.dtos.TransferMoneyRequestDTO;
import com.sk.SpringSecurity.dtos.UpdateUserProfileRequestDTO;
import com.sk.SpringSecurity.dtos.UserProfileResponseDTO;

public interface UserService {
	
	String addBeneficiary(AddBeneficiaryRequestDTO request);
	
	List<BeneficiaryResponseDTO> getMyBeneficiaries();
	
	String deleteBeneficiary(Long beneficiaryId);
	
	String transferMoney(TransferMoneyRequestDTO request);

	List<TransactionResponseDTO> getMyTransactions();

	TransactionReceiptResponseDTO getTransactionReceipt(String referenceNumber);

	List<MiniStatementResponseDTO> getMiniStatement(Long accountId);

	AccountDetailsDTO getAccountDetails(Long accountId);

	String openAccount(OpenAccountRequestDTO request);

	UserProfileResponseDTO updateProfile(UpdateUserProfileRequestDTO request);

	UserProfileResponseDTO getUserProfile();

	List<AccountSummaryDTO> getMyAccounts();
	
	
	
	
}
