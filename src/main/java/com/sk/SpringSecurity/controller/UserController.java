package com.sk.SpringSecurity.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.SpringSecurity.dtos.AddBeneficiaryRequestDTO;
import com.sk.SpringSecurity.dtos.AccountDetailsDTO;
import com.sk.SpringSecurity.dtos.AccountSummaryDTO;
import com.sk.SpringSecurity.dtos.BeneficiaryResponseDTO;
import com.sk.SpringSecurity.dtos.OpenAccountRequestDTO;
import com.sk.SpringSecurity.dtos.TransactionReceiptResponseDTO;
import com.sk.SpringSecurity.dtos.TransferMoneyRequestDTO;
import com.sk.SpringSecurity.dtos.UpdateUserProfileRequestDTO;
import com.sk.SpringSecurity.dtos.UserProfileResponseDTO;
import com.sk.SpringSecurity.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/beneficiaries")
	public ResponseEntity<String> addBeneficiary(
	        @RequestBody AddBeneficiaryRequestDTO request) {

	    String response = userService.addBeneficiary(request);

	    return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(response);
	}
	
	
	
	@GetMapping("/beneficiaries")
	public ResponseEntity<List<BeneficiaryResponseDTO>> getMyBeneficiaries() {

	    List<BeneficiaryResponseDTO> response =
	            userService.getMyBeneficiaries();

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}
	
	
	@DeleteMapping("/beneficiaries/{beneficiaryId}")
	public ResponseEntity<String> deleteBeneficiary(
	        @PathVariable Long beneficiaryId) {

	    String response =
	            userService.deleteBeneficiary(beneficiaryId);

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}
	
	@GetMapping("/transactions")
	public ResponseEntity<?> getMyTransactions() {

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(userService.getMyTransactions());
	}
	
	@GetMapping("/accounts/{accountId}/statement")
	public ResponseEntity<?> getMiniStatement(
	        @PathVariable Long accountId) {

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(userService.getMiniStatement(accountId));
	}
	
	
	@PostMapping("/transfer")
	public ResponseEntity<String> transferMoney(
	        @RequestBody TransferMoneyRequestDTO request) {

	    String response =
	            userService.transferMoney(request);

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}
	
	
	
	@GetMapping("/transactions/{referenceNumber}/receipt")
	public ResponseEntity<TransactionReceiptResponseDTO> getTransactionReceipt(
	        @PathVariable String referenceNumber) {

	    TransactionReceiptResponseDTO response =
	            userService.getTransactionReceipt(referenceNumber);

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}
	
	
	@GetMapping("/profile")
	public ResponseEntity<UserProfileResponseDTO> getUserProfile() {

	    UserProfileResponseDTO response =
	            userService.getUserProfile();

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}

	@GetMapping("/accounts")
	public ResponseEntity<List<AccountSummaryDTO>> getMyAccounts() {

	    List<AccountSummaryDTO> response = userService.getMyAccounts();

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}

	@PostMapping("/accounts/open")
	public ResponseEntity<String> openAccount(
	        @RequestBody OpenAccountRequestDTO request) {

	    String response = userService.openAccount(request);

	    return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(response);
	}

	@GetMapping("/accounts/{accountId}")
	public ResponseEntity<AccountDetailsDTO> getAccountDetails(
	        @PathVariable Long accountId) {

	    AccountDetailsDTO response = userService.getAccountDetails(accountId);

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}

	@PutMapping("/profile")
	public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
	        @RequestBody UpdateUserProfileRequestDTO request) {

	    UserProfileResponseDTO response = userService.updateProfile(request);

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(response);
	}
	

}
