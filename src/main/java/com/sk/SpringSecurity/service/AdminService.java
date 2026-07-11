package com.sk.SpringSecurity.service;

import java.util.List;

import com.sk.SpringSecurity.Entity.AdminAuditLog;
import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Transaction;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.Entity.UserVerification;
import com.sk.SpringSecurity.dtos.AdminAmountRequestDTO;
import com.sk.SpringSecurity.dtos.AdminTransactionReversalRequestDTO;

public interface AdminService {
	String approveKyc(Long userId);

    String rejectKyc(Long userId);

    List<UserVerification> getPendingKycRequests();

    List<User> getAllCustomers();

    List<BankAccount> getAllAccounts();

    List<Transaction> getAllTransactions();

    String freezeAccount(Long accountId);

    String unfreezeAccount(Long accountId);

    String deposit(Long accountId, AdminAmountRequestDTO request);

    String withdraw(Long accountId, AdminAmountRequestDTO request);

    String reverseTransaction(AdminTransactionReversalRequestDTO request);

    List<AdminAuditLog> getAuditLogs();

    User updateCustomer(Long userId, com.sk.SpringSecurity.dtos.AdminUserUpdateRequestDTO request);

    String postSavingsInterest();
}
