package com.sk.SpringSecurity.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.SpringSecurity.Entity.AccountLedger;
import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Beneficiary;
import com.sk.SpringSecurity.Entity.Transaction;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.dtos.AccountDetailsDTO;
import com.sk.SpringSecurity.dtos.AccountSummaryDTO;
import com.sk.SpringSecurity.dtos.AddBeneficiaryRequestDTO;
import com.sk.SpringSecurity.dtos.BeneficiaryResponseDTO;
import com.sk.SpringSecurity.dtos.MiniStatementResponseDTO;
import com.sk.SpringSecurity.dtos.OpenAccountRequestDTO;
import com.sk.SpringSecurity.dtos.TransactionReceiptResponseDTO;
import com.sk.SpringSecurity.dtos.TransactionResponseDTO;
import com.sk.SpringSecurity.dtos.TransferMoneyRequestDTO;
import com.sk.SpringSecurity.dtos.UpdateUserProfileRequestDTO;
import com.sk.SpringSecurity.dtos.UserProfileResponseDTO;
import com.sk.SpringSecurity.enums.AccountStatus;
import com.sk.SpringSecurity.enums.AccountType;
import com.sk.SpringSecurity.enums.LedgerType;
import com.sk.SpringSecurity.enums.TransactionMode;
import com.sk.SpringSecurity.enums.TransactionStatus;
import com.sk.SpringSecurity.enums.TransactionType;
import com.sk.SpringSecurity.enums.UserStatus;
import com.sk.SpringSecurity.repositories.AccountLedgerRepository;
import com.sk.SpringSecurity.repositories.AccountRepository;
import com.sk.SpringSecurity.repositories.BeneficiaryRepository;
import com.sk.SpringSecurity.repositories.TransactionRepository;
import com.sk.SpringSecurity.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final CurrentUserService currentUserService;
	private final NotificationService notificationService;
	private final BeneficiaryRepository beneficiaryRepository;
	private final AccountRepository accountRepository;
	private final AccountLedgerRepository accountLedgerRepository;
	private final TransactionRepository transactionRepository;

	@Override
	@Transactional
	public String addBeneficiary(AddBeneficiaryRequestDTO request) {

		User user = currentUserService.getCurrentUser();

		
		BankAccount account = accountRepository
				.findWithUserByAccountNumber(request.getAccountNumber())
				.orElseThrow(() -> new RuntimeException("Beneficiary account does not exist"));

		
		if (account.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("You cannot add your own account as beneficiary.");
		}

		
		if (beneficiaryRepository.existsByUserAndAccountNumber(
				user,
				request.getAccountNumber())) {

			throw new RuntimeException("Beneficiary already exists.");
		}

		
		Beneficiary beneficiary = Beneficiary.builder()
				.beneficiaryName(request.getBeneficiaryName())
				.nickname(request.getNickname())
				.accountNumber(request.getAccountNumber())
				.ifscCode(request.getIfscCode())
				.bankName(request.getBankName())
				.active(true)
				.user(user)
				.build();

		beneficiaryRepository.save(beneficiary);

		return "Beneficiary added successfully.";

	}

	@Override
	public List<BeneficiaryResponseDTO> getMyBeneficiaries() {

		User user = currentUserService.getCurrentUser();

		
		List<Beneficiary> beneficiaries = beneficiaryRepository.findByUserAndActiveTrue(user);

		
		List<BeneficiaryResponseDTO> response = new ArrayList<>();

		for (Beneficiary beneficiary : beneficiaries) {

			BeneficiaryResponseDTO dto = BeneficiaryResponseDTO.builder()
					.id(beneficiary.getId())
					.beneficiaryName(beneficiary.getBeneficiaryName())
					.nickname(beneficiary.getNickname())
					.accountNumber(beneficiary.getAccountNumber())
					.ifscCode(beneficiary.getIfscCode())
					.bankName(beneficiary.getBankName())
					.build();

			response.add(dto);
		}

		return response;
	}

	@Override
	@Transactional
	public String deleteBeneficiary(Long beneficiaryId) {

		User user = currentUserService.getCurrentUser();

		
		Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
				.orElseThrow(() -> new RuntimeException("Beneficiary not found"));

		
		if (!beneficiary.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("You are not authorized to delete this beneficiary.");
		}

		
		if (!beneficiary.isActive()) {
			throw new RuntimeException("Beneficiary is already deleted.");
		}

		
		beneficiary.setActive(false);

		beneficiaryRepository.save(beneficiary);

		return "Beneficiary deleted successfully.";
	}

	@Override
	@Transactional
	public String transferMoney(TransferMoneyRequestDTO request) {

	    User user = currentUserService.getCurrentUser();

	    String normalizedRequestId = null;

	    if (request.getRequestId() != null) {
	        normalizedRequestId = request.getRequestId().trim();
	    }

	    if (normalizedRequestId != null && !normalizedRequestId.isEmpty()) {

	        Transaction existingTransaction =
	                transactionRepository.findByIdempotencyKey(normalizedRequestId)
	                        .orElse(null);

	        if (existingTransaction != null) {

	            return "Duplicate transfer request already processed. Reference Number: "
	                    + existingTransaction.getReferenceNumber();

	        } else {

	            return processTransfer(user, request, normalizedRequestId);

	        }

	    } else {

	        return processTransfer(user, request, null);

	    }
	}

	@Transactional
	public String processTransfer(User user, TransferMoneyRequestDTO request, String normalizedRequestId) {
		BankAccount senderAccount = accountRepository
				.findAndLockWithUserById(request.getAccountId())
				.orElseThrow(() -> new RuntimeException("Sender account not found"));
		if (!senderAccount.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Unauthorized account access.");
		}

		if (senderAccount.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new RuntimeException("Sender account is not active.");
		}

		Beneficiary beneficiary = beneficiaryRepository.findByIdAndUserAndActiveTrue(request.getBeneficiaryId(), user)
				.orElseThrow(() -> new RuntimeException("Beneficiary not found"));

		BankAccount recieverAccount = accountRepository
				.findAndLockWithUserByAccountNumber(beneficiary.getAccountNumber())
				.orElseThrow(() -> new RuntimeException("Receiver account not found"));

		if (recieverAccount.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new RuntimeException("Receiver account is not active.");

		}

		if (request.getAmount() == null || request.getAmount() <= 0) {
			throw new RuntimeException("Invalid transfer amount");
		}

		if (senderAccount.getBalance() < request.getAmount()) {
			throw new RuntimeException("Insufficient Balance.");
		}

		long senderBalanceBefore = senderAccount.getBalance();
		long receiverBalanceBefore = recieverAccount.getBalance();

		senderAccount.setBalance(senderBalanceBefore - request.getAmount());
		recieverAccount.setBalance(receiverBalanceBefore + request.getAmount());

		accountRepository.save(senderAccount);
		accountRepository.save(recieverAccount);

		Transaction transaction = Transaction.builder()
				.referenceNumber(generateReferenceNumber())
				.idempotencyKey(normalizedRequestId)
				.fromAccount(senderAccount)
				.toAccount(recieverAccount)
				.amount(request.getAmount())
				.transactionType(TransactionType.TRANSFER)
				.transactionMode(TransactionMode.INTERNAL_TRANSFER)
				.status(TransactionStatus.SUCCESS)
				.remarks(request.getRemarks())
				.build();

		transactionRepository.save(transaction);

		AccountLedger senderLedger = AccountLedger.builder()
				.amount(request.getAmount())
				.balanceBefore(senderBalanceBefore)
				.balanceAfter(senderAccount.getBalance())
				.ledgerType(LedgerType.DEBIT)
				.account(senderAccount)
				.transaction(transaction)
				.build();
		accountLedgerRepository.save(senderLedger);

		AccountLedger receiverLedger = AccountLedger.builder()
				.amount(request.getAmount())
				.balanceBefore(receiverBalanceBefore)
				.balanceAfter(recieverAccount.getBalance())
				.ledgerType(LedgerType.CREDIT)
				.account(recieverAccount)
				.transaction(transaction)
				.build();

		accountLedgerRepository.save(receiverLedger);

		String senderMessage = "Rs." + request.getAmount()
				+ " debited from account " + senderAccount.getAccountNumber()
				+ ". Ref: " + transaction.getReferenceNumber();

		String receiverMessage = "Rs." + request.getAmount()
				+ " credited to account " + recieverAccount.getAccountNumber()
				+ ". Ref: " + transaction.getReferenceNumber();

		
		return "Money transferred successfully. Reference Number: "
				+ transaction.getReferenceNumber();
	}

	private String generateReferenceNumber() {
		return "TXN" + System.currentTimeMillis();
	}

	@Override
	public List<TransactionResponseDTO> getMyTransactions() {

		User user = currentUserService.getCurrentUser();

		List<BankAccount> accounts = accountRepository.findByUserId(user.getId());
		List<Transaction> transactions = transactionRepository.findUserTransactions(user.getId());

		List<TransactionResponseDTO> response = new ArrayList<>();

		for (Transaction transaction : transactions) {
			TransactionResponseDTO dto = new TransactionResponseDTO();

			dto.setReferenceNumber(transaction.getReferenceNumber());
			dto.setAmount(transaction.getAmount());
			dto.setStatus(transaction.getStatus().name());
			dto.setRemarks(transaction.getRemarks());
			dto.setTransactionTime(transaction.getTransactionTime());
			dto.setFromAccountNumber(
					transaction.getFromAccount() == null ? "N/A" : transaction.getFromAccount().getAccountNumber());
			dto.setToAccountNumber(
					transaction.getToAccount() == null ? "N/A" : transaction.getToAccount().getAccountNumber());

			if (accounts.contains(transaction.getFromAccount())) {

				dto.setTransactionType("DEBIT");
				dto.setTransactionTitle("Paid To");

				Beneficiary beneficiary = beneficiaryRepository
						.findByUserAndAccountNumber(
								user,
								transaction.getToAccount().getAccountNumber())
						.orElse(null);
				if (beneficiary != null) {
					dto.setBeneficiaryNickname(beneficiary.getNickname());
				} else {
					dto.setBeneficiaryNickname(
							transaction.getToAccount()
									.getUser()
									.getFullName());
				}

			} else {

				dto.setTransactionType("CREDIT");
				dto.setTransactionTitle("Received From");

				Beneficiary beneficiary = beneficiaryRepository
						.findByUserAndAccountNumber(
								user,
								transaction.getFromAccount().getAccountNumber())
						.orElse(null);

				if (beneficiary != null) {
					dto.setBeneficiaryNickname(beneficiary.getNickname());
				} else {
					dto.setBeneficiaryNickname(
							transaction.getFromAccount()
									.getUser()
									.getFullName());
				}
			}

			response.add(dto);
		}
		return response;
	}

	@Override
	public TransactionReceiptResponseDTO getTransactionReceipt(String referenceNumber) {

		User user = currentUserService.getCurrentUser();
		checkApprovedUser(user);

		List<BankAccount> accounts = accountRepository.findByUserId(user.getId());

		Transaction transaction = transactionRepository
				.findReceiptTransaction(referenceNumber)
				.orElseThrow(() -> new RuntimeException("Transaction not found"));

		
		if (!accounts.contains(transaction.getFromAccount())
				&& !accounts.contains(transaction.getToAccount())) {

			throw new RuntimeException("You are not authorized to view this receipt.");
		}

		BankAccount myAccount;

		if (accounts.contains(transaction.getFromAccount())) {
			myAccount = transaction.getFromAccount();
		} else {
			myAccount = transaction.getToAccount();
		}

		AccountLedger ledger = accountLedgerRepository
				.findByTransactionAndAccount(transaction, myAccount)
				.orElseThrow(() -> new RuntimeException("Ledger not found"));

		String fromAccNum = transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber()
				: "N/A";
		String fromAccHolder = (transaction.getFromAccount() != null && transaction.getFromAccount().getUser() != null)
				? transaction.getFromAccount().getUser().getFullName()
				: "N/A";
		String toAccNum = transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : "N/A";
		String toAccHolder = (transaction.getToAccount() != null && transaction.getToAccount().getUser() != null)
				? transaction.getToAccount().getUser().getFullName()
				: "N/A";

		return TransactionReceiptResponseDTO.builder()
				.referenceNumber(transaction.getReferenceNumber())
				.amount(transaction.getAmount())
				.status(transaction.getStatus().name())
				.transactionType(ledger.getLedgerType().name())
				.transactionMode(transaction.getTransactionMode().name())
				.fromAccountNumber(fromAccNum)
				.fromAccountHolderName(fromAccHolder)
				.toAccountNumber(toAccNum)
				.toAccountHolderName(toAccHolder)
				.balanceBefore(ledger.getBalanceBefore())
				.balanceAfter(ledger.getBalanceAfter())
				.remarks(transaction.getRemarks())
				.transactionTime(transaction.getTransactionTime())
				.build();
	}

	@Override
	public List<MiniStatementResponseDTO> getMiniStatement(Long accountId) {

		User user = currentUserService.getCurrentUser();

		BankAccount account = accountRepository.findWithUserById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (!account.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Unathorized account access");
		}

		List<AccountLedger> ledgers = accountLedgerRepository.findStatementByAccountId(account.getId());

		List<MiniStatementResponseDTO> response = new ArrayList<>();

		for (AccountLedger ledger : ledgers) {

			MiniStatementResponseDTO dto = MiniStatementResponseDTO.builder()
					.transactionReferenceNumber(ledger.getTransaction().getReferenceNumber())
					.transactionType(ledger.getLedgerType().name())
					.amount(ledger.getAmount())
					.balanceAfter(ledger.getBalanceAfter())
					.remarks(ledger.getTransaction().getRemarks())
					.transactionTime(ledger.getTransaction().getTransactionTime())
					.build();

			response.add(dto);
		}

		return response;
	}

	@Override
	public UserProfileResponseDTO getUserProfile() {

		User user = currentUserService.getCurrentUserWithVerification();

		return UserProfileResponseDTO.builder()
				
				.customerId(user.getCustomerId())
				.fullName(user.getFullName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.userStatus(user.getStatus().name())

				
				.dateOfBirth(user.getDateOfBirth())
				.gender(user.getGender())
				.occupation(user.getOccupation())
				.annualIncome(user.getAnnualIncome())
				.nationality(user.getNationality())

				
				.houseNo(user.getHouseNo())
				.street(user.getStreet())
				.city(user.getCity())
				.district(user.getDistrict())
				.state(user.getState())
				.country(user.getCountry())
				.pincode(user.getPincode())

				
				.kycStatus(user.getVerification().getKycStatus().name())
				.emailVerified(user.getVerification().isEmailVerified())
				.mobileVerified(user.getVerification().isMobileVerified())
				.aadhaarVerified(user.getVerification().isAadhaarVerified())
				.panVerified(user.getVerification().isPanVerified())

				
				.profilePhotoPath(user.getVerification().getProfilePhotoPath())

				.build();
	}

	@Override
	public List<AccountSummaryDTO> getMyAccounts() {

		Long userId = currentUserService.getCurrentUserId();

		List<BankAccount> accounts = accountRepository.findByUserId(userId);

		List<AccountSummaryDTO> response = new ArrayList<>();

		for (BankAccount account : accounts) {
			AccountSummaryDTO dto = AccountSummaryDTO.builder()
					.accountId(account.getId())
					.id(account.getId())
					.accountType(account.getAccountType().name())
					.accountNumber(account.getAccountNumber())
					.balance(account.getBalance())
					.accountStatus(account.getAccountStatus().name())
					.branchName(account.getBranchName())
					.ifscCode(account.getIfscCode())
					.currency(account.getCurrency())
					.build();

			response.add(dto);
		}

		return response;
	}

	@Override
	public AccountDetailsDTO getAccountDetails(Long accountId) {

		User user = currentUserService.getCurrentUser();

		BankAccount account = accountRepository.findWithUserById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (!account.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Unauthorized account access");
		}

		return AccountDetailsDTO.builder()
				.accountId(account.getId())
				.accountNumber(account.getAccountNumber())
				.accountType(account.getAccountType().name())
				.accountStatus(account.getAccountStatus().name())
				.balance(account.getBalance())
				.currency(account.getCurrency())
				.ifscCode(account.getIfscCode())
				.branchName(account.getBranchName())
				.openedAt(account.getOpenedAt())
				.closedAt(account.getClosedAt())
				.build();
	}

	@Override
	@Transactional
	public String openAccount(OpenAccountRequestDTO request) {

		User user = currentUserService.getCurrentUser();

		if (user.getStatus() != UserStatus.APPROVED) {
			throw new RuntimeException("Only approved users can open accounts.");
		}

		if (request.getInitialDeposit() == null || request.getInitialDeposit() < 0) {
			throw new RuntimeException("Initial deposit must be zero or positive.");
		}

		AccountType accountType = AccountType.SAVINGS;
		if (request.getAccountType() != null && request.getAccountType().equalsIgnoreCase("CURRENT")) {
			accountType = AccountType.CURRENT;
		}

		BankAccount account = BankAccount.builder()
				.accountNumber(generateAccountNumber())
				.accountType(accountType)
				.accountStatus(AccountStatus.ACTIVE)
				.balance(request.getInitialDeposit() == null ? 0L : request.getInitialDeposit())
				.branchName("Kadapa main Branch")
				.ifscCode("GSKA0009398")
				.currency("INR")
				.user(user)
				.build();

		accountRepository.save(account);

		if (account.getBalance() > 0) {
			Transaction transaction = Transaction.builder()
					.referenceNumber(generateReferenceNumber())
					.fromAccount(null)
					.toAccount(account)
					.amount(account.getBalance())
					.transactionType(TransactionType.CREDIT)
					.transactionMode(TransactionMode.DEPOSIT)
					.status(TransactionStatus.SUCCESS)
					.remarks("Initial deposit for new account")
					.build();

			transactionRepository.save(transaction);

			AccountLedger ledger = AccountLedger.builder()
					.amount(account.getBalance())
					.balanceBefore(0L)
					.balanceAfter(account.getBalance())
					.ledgerType(LedgerType.CREDIT)
					.account(account)
					.transaction(transaction)
					.build();

			accountLedgerRepository.save(ledger);
		}

		return "Account opened successfully with account number " + account.getAccountNumber();
	}

	@Override
	@Transactional
	public UserProfileResponseDTO updateProfile(UpdateUserProfileRequestDTO request) {

		User user = currentUserService.getCurrentUser();

		if (request.getFullName() != null) {
			user.setFullName(request.getFullName());
		}
		if (request.getPhone() != null) {
			user.setPhone(request.getPhone());
		}
		if (request.getOccupation() != null) {
			user.setOccupation(request.getOccupation());
		}
		if (request.getAnnualIncome() != null) {
			user.setAnnualIncome(request.getAnnualIncome());
		}
		if (request.getNationality() != null) {
			user.setNationality(request.getNationality());
		}
		if (request.getHouseNo() != null) {
			user.setHouseNo(request.getHouseNo());
		}
		if (request.getStreet() != null) {
			user.setStreet(request.getStreet());
		}
		if (request.getCity() != null) {
			user.setCity(request.getCity());
		}
		if (request.getDistrict() != null) {
			user.setDistrict(request.getDistrict());
		}
		if (request.getState() != null) {
			user.setState(request.getState());
		}
		if (request.getCountry() != null) {
			user.setCountry(request.getCountry());
		}
		if (request.getPincode() != null) {
			user.setPincode(request.getPincode());
		}
		if (request.getDateOfBirth() != null) {
			user.setDateOfBirth(request.getDateOfBirth());
		}
		if (request.getGender() != null) {
			user.setGender(request.getGender());
		}

		userRepository.save(user);

		return UserProfileResponseDTO.builder()
				.customerId(user.getCustomerId())
				.fullName(user.getFullName())
				.email(user.getEmail())
				.phone(user.getPhone())
				.userStatus(user.getStatus().name())
				.dateOfBirth(user.getDateOfBirth())
				.gender(user.getGender())
				.occupation(user.getOccupation())
				.annualIncome(user.getAnnualIncome())
				.nationality(user.getNationality())
				.houseNo(user.getHouseNo())
				.street(user.getStreet())
				.city(user.getCity())
				.district(user.getDistrict())
				.state(user.getState())
				.country(user.getCountry())
				.pincode(user.getPincode())
				.kycStatus(user.getVerification() == null ? null : user.getVerification().getKycStatus().name())
				.emailVerified(user.getVerification() != null && user.getVerification().isEmailVerified())
				.mobileVerified(user.getVerification() != null && user.getVerification().isMobileVerified())
				.aadhaarVerified(user.getVerification() != null && user.getVerification().isAadhaarVerified())
				.panVerified(user.getVerification() != null && user.getVerification().isPanVerified())
				.profilePhotoPath(user.getVerification() == null ? null : user.getVerification().getProfilePhotoPath())
				.build();
	}

	private String generateAccountNumber() {
		return "AC" + System.currentTimeMillis();
	}

	private void checkApprovedUser(User user) {

		if (user.getStatus() != UserStatus.APPROVED) {

			throw new RuntimeException(
					"Your account is not approved. Complete KYC first.");
		}
	}

}
