package com.sk.SpringSecurity.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.sk.SpringSecurity.Entity.AccountLedger;
import com.sk.SpringSecurity.Entity.AdminAuditLog;
import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Transaction;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.Entity.UserVerification;
import com.sk.SpringSecurity.dtos.AdminAmountRequestDTO;
import com.sk.SpringSecurity.dtos.AdminTransactionReversalRequestDTO;
import com.sk.SpringSecurity.enums.AccountStatus;
import com.sk.SpringSecurity.enums.AccountType;
import com.sk.SpringSecurity.enums.KycStatus;
import com.sk.SpringSecurity.enums.LedgerType;
import com.sk.SpringSecurity.enums.TransactionMode;
import com.sk.SpringSecurity.enums.TransactionStatus;
import com.sk.SpringSecurity.enums.TransactionType;
import com.sk.SpringSecurity.enums.UserStatus;
import com.sk.SpringSecurity.repositories.AccountLedgerRepository;
import com.sk.SpringSecurity.repositories.AccountRepository;
import com.sk.SpringSecurity.repositories.AdminAuditLogRepository;
import com.sk.SpringSecurity.repositories.TransactionRepository;
import com.sk.SpringSecurity.repositories.UserRepository;
import com.sk.SpringSecurity.repositories.UserVerificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final UserVerificationRepository userVerificationRepository;
	private final TransactionRepository transactionRepository;
	private final AccountLedgerRepository accountLedgerRepository;
	private final NotificationService notificationService;
	private final CurrentUserService currentUserService;
	private final AdminAuditLogRepository adminAuditLogRepository;

	private String generateAccountNumber() {
		Random random = new Random();
		String accountNumber;
		do {
			accountNumber = String.valueOf(1000000000L + (long) (random.nextDouble() * 9000000000L));

		} while (accountRepository.existsByAccountNumber(accountNumber));
		return accountNumber;
	}

	private String generateCustomerId() {

		Random random = new Random();
		String customerId;

		do {
			customerId = "CIF" + (100000 + random.nextInt(900000));
		} while (userRepository.existsByCustomerId(customerId));

		return customerId;
	}

	@Override
	@Transactional
	public String approveKyc(Long userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		UserVerification verification = userVerificationRepository
				.findByUser(user)
				.orElseThrow(() -> new RuntimeException("User verification not found"));

		if (verification.getKycStatus() == KycStatus.VERIFIED) {
			throw new RuntimeException("KYC is already approved.");
		}

		if (verification.getKycStatus() == KycStatus.PENDING) {
			throw new RuntimeException("KYC is not ready for approval.");
		}

		
		verification.setAadhaarVerified(true);
		verification.setPanVerified(true);

		user.setCustomerId(generateCustomerId());

		user.setStatus(UserStatus.APPROVED);

		verification.setKycStatus(KycStatus.VERIFIED);

		verification.setVerifiedBy("ADMIN");

		verification.setVerifiedAt(LocalDateTime.now());

		BankAccount account = BankAccount.builder()
				.accountNumber(generateAccountNumber())
				.accountType(AccountType.SAVINGS)
				.accountStatus(AccountStatus.ACTIVE)
				.balance(0L)
				.branchName("Kadapa main Branch")
				.ifscCode("GSKA0009398")
				.currency("INR")
				.user(user)
				.build();

		userRepository.save(user);

		userVerificationRepository.save(verification);

		accountRepository.save(account);

		recordAudit("APPROVE_KYC", "USER", userId, "KYC approved and savings account created");

		return "KYC approved successfully. Savings account created.";
	}

	@Override
	@Transactional
	public String rejectKyc(Long userId) {

		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		
		UserVerification verification = userVerificationRepository
				.findByUser(user)
				.orElseThrow(() -> new RuntimeException("User verification not found"));

		
		if (verification.getKycStatus() == KycStatus.REJECTED) {
			throw new RuntimeException("KYC is already rejected.");
		}

		
		if (verification.getKycStatus() == KycStatus.VERIFIED) {
			throw new RuntimeException("Approved KYC cannot be rejected.");
		}

		
		verification.setKycStatus(KycStatus.REJECTED);
		verification.setVerifiedBy("ADMIN");
		verification.setVerifiedAt(LocalDateTime.now());

		
		user.setStatus(UserStatus.REJECTED);

		
		userRepository.save(user);
		userVerificationRepository.save(verification);

		recordAudit("REJECT_KYC", "USER", userId, "KYC rejected by admin");

		return "KYC rejected successfully.";
	}

	@Override
	public List<UserVerification> getPendingKycRequests() {
		return userVerificationRepository.findPendingKycRequests();
	}

	@Override
	public List<User> getAllCustomers() {
		return userRepository.findAll();
	}

	@Override
	public List<BankAccount> getAllAccounts() {

		return accountRepository.findAll();

	}

	@Override
	public List<Transaction> getAllTransactions() {

		return transactionRepository.findAll();

	}

	@Override
	public List<AdminAuditLog> getAuditLogs() {
		return adminAuditLogRepository.findAllByOrderByCreatedAtDesc();
	}

	@Override
	@Transactional
	public String freezeAccount(Long accountId) {
		BankAccount account = accountRepository.findWithUserById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (account.getAccountStatus() == AccountStatus.CLOSED) {
			throw new RuntimeException("Closed account cannot be frozen.");
		}

		if (account.getAccountStatus() == AccountStatus.FROZEN) {
			throw new RuntimeException("Account is already frozen.");
		}

		account.setAccountStatus(AccountStatus.FROZEN);
		accountRepository.save(account);

		recordAudit("FREEZE_ACCOUNT", "ACCOUNT", accountId, "Account frozen by admin");

		notificationService.sendTransactionAlerts(
				account.getUser(),
				"Your account " + account.getAccountNumber() + " has been frozen by bank admin.");

		return "Account frozen successfully.";
	}

	@Override
	@Transactional
	public String unfreezeAccount(Long accountId) {
		BankAccount account = accountRepository.findWithUserById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (account.getAccountStatus() != AccountStatus.FROZEN) {
			throw new RuntimeException("Only frozen accounts can be unfrozen.");
		}

		account.setAccountStatus(AccountStatus.ACTIVE);
		accountRepository.save(account);

		recordAudit("UNFREEZE_ACCOUNT", "ACCOUNT", accountId, "Account unfrozen by admin");

		notificationService.sendTransactionAlerts(
				account.getUser(),
				"Your account " + account.getAccountNumber() + " has been activated by bank admin.");

		return "Account unfrozen successfully.";
	}

	@Override
	@Transactional
	public String deposit(Long accountId, AdminAmountRequestDTO request) {
		BankAccount account = accountRepository.findWithUserById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (account.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new RuntimeException("Amount can be deposited only into active accounts.");
		}

		validateAmount(request.getAmount());

		long balanceBefore = account.getBalance();
		account.setBalance(balanceBefore + request.getAmount());
		accountRepository.save(account);

		Transaction transaction = Transaction.builder()
				.referenceNumber(generateReferenceNumber())
				.fromAccount(null)
				.toAccount(account)
				.amount(request.getAmount())
				.transactionType(TransactionType.CREDIT)
				.transactionMode(TransactionMode.DEPOSIT)
				.status(TransactionStatus.SUCCESS)
				.remarks(request.getRemarks())
				.build();
		transactionRepository.save(transaction);

		saveLedger(account, transaction, request.getAmount(), balanceBefore, account.getBalance(), LedgerType.CREDIT);

		recordAudit("DEPOSIT", "ACCOUNT", accountId, "Admin deposit of Rs." + request.getAmount());

		notificationService.sendTransactionAlerts(
				account.getUser(),
				"Rs." + request.getAmount() + " deposited to account "
						+ account.getAccountNumber() + ". Ref: " + transaction.getReferenceNumber());

		return "Amount deposited successfully. Reference Number: " + transaction.getReferenceNumber();
	}

	@Override
	@Transactional
	public String withdraw(Long accountId, AdminAmountRequestDTO request) {
		BankAccount account = accountRepository.findWithUserById(accountId)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (account.getAccountStatus() != AccountStatus.ACTIVE) {
			throw new RuntimeException("Amount can be withdrawn only from active accounts.");
		}

		validateAmount(request.getAmount());

		if (account.getBalance() < request.getAmount()) {
			throw new RuntimeException("Insufficient balance.");
		}

		long balanceBefore = account.getBalance();
		account.setBalance(balanceBefore - request.getAmount());
		accountRepository.save(account);

		Transaction transaction = Transaction.builder()
				.referenceNumber(generateReferenceNumber())
				.fromAccount(account)
				.toAccount(null)
				.amount(request.getAmount())
				.transactionType(TransactionType.DEBIT)
				.transactionMode(TransactionMode.WITHDRAWAL)
				.status(TransactionStatus.SUCCESS)
				.remarks(request.getRemarks())
				.build();
		transactionRepository.save(transaction);

		saveLedger(account, transaction, request.getAmount(), balanceBefore, account.getBalance(), LedgerType.DEBIT);

		recordAudit("WITHDRAW", "ACCOUNT", accountId, "Admin withdrawal of Rs." + request.getAmount());

		notificationService.sendTransactionAlerts(
				account.getUser(),
				"Rs." + request.getAmount() + " withdrawn from account "
						+ account.getAccountNumber() + ". Ref: " + transaction.getReferenceNumber());

		return "Amount withdrawn successfully. Reference Number: " + transaction.getReferenceNumber();
	}

	@Override
	@Transactional
	public String reverseTransaction(AdminTransactionReversalRequestDTO request) {
		Transaction original = transactionRepository.findByReferenceNumber(request.getReferenceNumber())
				.orElseThrow(() -> new RuntimeException("Transaction not found"));

		if (original.getStatus() != TransactionStatus.SUCCESS) {
			throw new RuntimeException("Only successful transactions can be reversed.");
		}

		BankAccount debitAccount = original.getFromAccount();
		BankAccount creditAccount = original.getToAccount();

		if (debitAccount == null || creditAccount == null) {
			throw new RuntimeException("Only transfers can be reversed.");
		}

		if (creditAccount.getBalance() < original.getAmount()) {
			throw new RuntimeException("Receiver account has insufficient balance to reverse the transaction.");
		}

		long debitBefore = debitAccount.getBalance();
		long creditBefore = creditAccount.getBalance();

		debitAccount.setBalance(debitBefore + original.getAmount());
		creditAccount.setBalance(creditBefore - original.getAmount());

		accountRepository.save(debitAccount);
		accountRepository.save(creditAccount);

		original.setStatus(TransactionStatus.REVERSED);
		original.setRemarks(original.getRemarks() + " | Reversed: " + request.getReason());
		transactionRepository.save(original);

		Transaction reversal = Transaction.builder()
				.referenceNumber(generateReferenceNumber())
				.fromAccount(creditAccount)
				.toAccount(debitAccount)
				.amount(original.getAmount())
				.transactionType(TransactionType.TRANSFER)
				.transactionMode(TransactionMode.INTERNAL_TRANSFER)
				.status(TransactionStatus.SUCCESS)
				.remarks("Reversal of " + original.getReferenceNumber() + ". Reason: " + request.getReason())
				.build();

		transactionRepository.save(reversal);

		saveLedger(creditAccount, reversal, original.getAmount(), creditBefore, creditAccount.getBalance(),
				LedgerType.DEBIT);
		saveLedger(debitAccount, reversal, original.getAmount(), debitBefore, debitAccount.getBalance(),
				LedgerType.CREDIT);

		recordAudit("REVERSE_TRANSACTION", "TRANSACTION", original.getId(),
				"Reversed transaction " + original.getReferenceNumber() + ". Reason: " + request.getReason());

		return "Transaction reversed successfully. Reversal Reference: " + reversal.getReferenceNumber();
	}

	private void recordAudit(String action, String targetType, Long targetId, String details) {
		User actor = currentUserService.getCurrentUser();
		AdminAuditLog log = AdminAuditLog.builder()
				.action(action)
				.targetType(targetType)
				.targetId(targetId)
				.actor(actor.getEmail())
				.details(details)
				.build();
		adminAuditLogRepository.save(log);
	}

	private void validateAmount(Long amount) {
		if (amount == null || amount <= 0) {
			throw new RuntimeException("Invalid amount.");
		}
	}

	private String generateReferenceNumber() {
		return "TXN" + System.currentTimeMillis();
	}

	private void saveLedger(
			BankAccount account,
			Transaction transaction,
			Long amount,
			Long balanceBefore,
			Long balanceAfter,
			LedgerType ledgerType) {

		AccountLedger ledger = AccountLedger.builder()
				.account(account)
				.transaction(transaction)
				.amount(amount)
				.balanceBefore(balanceBefore)
				.balanceAfter(balanceAfter)
				.ledgerType(ledgerType)
				.build();

		accountLedgerRepository.save(ledger);
	}

	@Override
	@Transactional
	public User updateCustomer(Long userId, com.sk.SpringSecurity.dtos.AdminUserUpdateRequestDTO request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Customer not found."));

		if (request.getFullName() != null)
			user.setFullName(request.getFullName());

		
		if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
			if (userRepository.findByEmail(request.getEmail()).isPresent()) {
				throw new RuntimeException("Email already in use.");
			}
			user.setEmail(request.getEmail());
		}

		
		if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
			if (userRepository.findByPhone(request.getPhone()).isPresent()) {
				throw new RuntimeException("Phone number already in use.");
			}
			user.setPhone(request.getPhone());
		}

		if (request.getOccupation() != null)
			user.setOccupation(request.getOccupation());
		if (request.getAnnualIncome() != null)
			user.setAnnualIncome(request.getAnnualIncome());
		if (request.getNationality() != null)
			user.setNationality(request.getNationality());
		if (request.getHouseNo() != null)
			user.setHouseNo(request.getHouseNo());
		if (request.getStreet() != null)
			user.setStreet(request.getStreet());
		if (request.getCity() != null)
			user.setCity(request.getCity());
		if (request.getDistrict() != null)
			user.setDistrict(request.getDistrict());
		if (request.getState() != null)
			user.setState(request.getState());
		if (request.getCountry() != null)
			user.setCountry(request.getCountry());
		if (request.getPincode() != null)
			user.setPincode(request.getPincode());
		if (request.getDateOfBirth() != null)
			user.setDateOfBirth(request.getDateOfBirth());
		if (request.getGender() != null)
			user.setGender(request.getGender());

		if (request.getAadhaarNumber() != null)
			user.setAadhaarNumber(request.getAadhaarNumber());

		if (request.getStatus() != null) {
			user.setStatus(request.getStatus());
			if (request.getStatus() == com.sk.SpringSecurity.enums.UserStatus.APPROVED) {
				if (user.getCustomerId() == null) {
					user.setCustomerId(generateCustomerId());
				}
				if (accountRepository.findByUserId(user.getId()).isEmpty()) {
					BankAccount account = BankAccount.builder()
							.accountNumber(generateAccountNumber())
							.accountType(AccountType.SAVINGS)
							.accountStatus(AccountStatus.ACTIVE)
							.balance(0L)
							.branchName("Kadapa main Branch")
							.ifscCode("GSKA0009398")
							.currency("INR")
							.user(user)
							.build();
					accountRepository.save(account);
				}
			}
		}

		
		if (user.getVerification() != null) {
			UserVerification verification = user.getVerification();
			if (request.getAadhaarNumber() != null)
				verification.setAadhaarNumber(request.getAadhaarNumber());
			if (request.getPanNumber() != null)
				verification.setPanNumber(request.getPanNumber());
			if (request.getStatus() != null) {
				if (request.getStatus() == com.sk.SpringSecurity.enums.UserStatus.APPROVED) {
					verification.setKycStatus(com.sk.SpringSecurity.enums.KycStatus.VERIFIED);
					verification.setAadhaarVerified(true);
					verification.setPanVerified(true);
				} else if (request.getStatus() == com.sk.SpringSecurity.enums.UserStatus.REJECTED) {
					verification.setKycStatus(com.sk.SpringSecurity.enums.KycStatus.REJECTED);
				} else {
					verification.setKycStatus(com.sk.SpringSecurity.enums.KycStatus.PENDING);
				}
			}
			userVerificationRepository.save(verification);
		}

		User updatedUser = userRepository.save(user);
		recordAudit("UPDATE_CUSTOMER_PROFILE", "USER", userId, "Updated customer details: " + user.getEmail());
		return updatedUser;
	}

	@Override
	@Transactional
	public String postSavingsInterest() {
		List<BankAccount> accounts = accountRepository.findAll();
		long totalInterestCredited = 0L;
		int processedCount = 0;

		for (BankAccount account : accounts) {
			if (account.getAccountStatus() == AccountStatus.ACTIVE && account.getAccountType() == AccountType.SAVINGS) {
				long balance = account.getBalance();
				if (balance > 0) {
					
					long interest = Math.round(balance * 0.03 / 12.0);
					if (interest > 0) {
						account.setBalance(balance + interest);
						accountRepository.save(account);

						Transaction transaction = Transaction.builder()
								.referenceNumber(generateReferenceNumber())
								.fromAccount(null)
								.toAccount(account)
								.amount(interest)
								.transactionType(TransactionType.CREDIT)
								.transactionMode(TransactionMode.INTERNAL_TRANSFER)
								.status(TransactionStatus.SUCCESS)
								.remarks("Monthly savings interest posting (3.0% p.a.)")
								.build();
						transactionRepository.save(transaction);

						saveLedger(account, transaction, interest, balance, account.getBalance(), LedgerType.CREDIT);

						notificationService.sendTransactionAlerts(
								account.getUser(),
								"Rs." + interest + " Interest Credited to savings account "
										+ account.getAccountNumber() + ". Ref: " + transaction.getReferenceNumber());

						totalInterestCredited += interest;
						processedCount++;
					}
				}
			}
		}

		recordAudit("POST_INTEREST_BATCH", "SYSTEM", 0L,
				"Executed savings interest accrual. Accounts: " + processedCount + ", Total Credited: Rs."
						+ totalInterestCredited);

		return "Interest batch completed successfully. Processed " + processedCount + " accounts. Total credited: Rs."
				+ totalInterestCredited;
	}

}
