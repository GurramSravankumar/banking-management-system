package com.sk.SpringSecurity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.SpringSecurity.Entity.AdminAuditLog;
import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Transaction;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.Entity.UserVerification;
import com.sk.SpringSecurity.dtos.AdminAmountRequestDTO;
import com.sk.SpringSecurity.dtos.AdminTransactionReversalRequestDTO;
import com.sk.SpringSecurity.service.AdminService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {


    private final AdminService adminService;


    
    @PutMapping("/kyc/{userId}/approve")
    public ResponseEntity<String> approveKyc(
            @PathVariable Long userId) {


        String response = adminService.approveKyc(userId);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    
    @PutMapping("/kyc/{userId}/reject")
    public ResponseEntity<String> rejectKyc(
            @PathVariable Long userId) {


        String response = adminService.rejectKyc(userId);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    
    @GetMapping("/kyc/pending")
    public ResponseEntity<List<UserVerification>> getPendingKycRequests() {


        List<UserVerification> response =
                adminService.getPendingKycRequests();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



    
    @GetMapping("/customers")
    public ResponseEntity<List<User>> getAllCustomers() {


        List<User> response =
                adminService.getAllCustomers();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }




    
    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccount>> getAllAccounts() {


        List<BankAccount> response =
                adminService.getAllAccounts();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }




    
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {


        List<Transaction> response =
                adminService.getAllTransactions();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/accounts/{accountId}/freeze")
    public ResponseEntity<String> freezeAccount(@PathVariable Long accountId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.freezeAccount(accountId));
    }

    @PutMapping("/accounts/{accountId}/unfreeze")
    public ResponseEntity<String> unfreezeAccount(@PathVariable Long accountId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.unfreezeAccount(accountId));
    }

    @PostMapping("/accounts/{accountId}/deposit")
    public ResponseEntity<String> deposit(
            @PathVariable Long accountId,
            @RequestBody AdminAmountRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.deposit(accountId, request));
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    public ResponseEntity<String> withdraw(
            @PathVariable Long accountId,
            @RequestBody AdminAmountRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.withdraw(accountId, request));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AdminAuditLog>> getAuditLogs() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.getAuditLogs());
    }

    @PostMapping("/transactions/reverse")
    public ResponseEntity<String> reverseTransaction(
            @RequestBody AdminTransactionReversalRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.reverseTransaction(request));
    }

    @PutMapping("/customers/{userId}")
    public ResponseEntity<User> updateCustomer(
            @PathVariable Long userId,
            @RequestBody com.sk.SpringSecurity.dtos.AdminUserUpdateRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.updateCustomer(userId, request));
    }

    @PostMapping("/batch/post-interest")
    public ResponseEntity<String> postSavingsInterest() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(adminService.postSavingsInterest());
    }

}
