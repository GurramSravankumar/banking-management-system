package com.sk.SpringSecurity.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.SpringSecurity.Entity.AccountLedger;
import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Loan;
import com.sk.SpringSecurity.Entity.Transaction;
import com.sk.SpringSecurity.enums.AccountStatus;
import com.sk.SpringSecurity.enums.LedgerType;
import com.sk.SpringSecurity.enums.LoanStatus;
import com.sk.SpringSecurity.enums.TransactionMode;
import com.sk.SpringSecurity.enums.TransactionStatus;
import com.sk.SpringSecurity.enums.TransactionType;
import com.sk.SpringSecurity.repositories.AccountLedgerRepository;
import com.sk.SpringSecurity.repositories.AccountRepository;
import com.sk.SpringSecurity.repositories.LoanRepository;
import com.sk.SpringSecurity.repositories.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class LoanAutopayScheduler {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountLedgerRepository accountLedgerRepository;

    
    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional
    public void processAutopay() {
        System.out.println("--- Starting Loan Autopay Process Cycle ---");
        List<Loan> activeLoans = loanRepository.findByStatus(LoanStatus.APPROVED);

        for (Loan loan : activeLoans) {
            if (loan.getOutstandingBalance() <= 0) {
                loan.setStatus(LoanStatus.PAID);
                loanRepository.save(loan);
                continue;
            }

            LocalDateTime now = LocalDateTime.now();
            
            if (loan.getLastPaymentAt() != null && loan.getLastPaymentAt().plusSeconds(60).isAfter(now)) {
                continue; 
            }

            
            List<BankAccount> debtorAccounts = accountRepository.findByUserId(loan.getUser().getId());
            BankAccount payAccount = debtorAccounts.stream()
                    .filter(a -> a.getAccountStatus() == AccountStatus.ACTIVE)
                    .findFirst()
                    .orElse(null);

            double emiToPay = loan.getEmiAmount();
            
            
            if (loan.getOutstandingBalance() < emiToPay) {
                emiToPay = loan.getOutstandingBalance();
            }
            long emiLong = Math.round(emiToPay);

            if (payAccount == null) {
                System.err.println("No active bank account found for User: " + loan.getUser().getFullName()
                        + " to process Loan ID: " + loan.getId());
                continue;
            }

            if (payAccount.getBalance() < emiLong) {
                System.err.println("Insufficient balance in Acc: " + payAccount.getAccountNumber() + " for Loan ID: "
                        + loan.getId());
                
                
                loan.setLastPaymentAt(now);
                loanRepository.save(loan);
                continue;
            }

            
            long balanceBefore = payAccount.getBalance();
            payAccount.setBalance(balanceBefore - emiLong);
            accountRepository.save(payAccount);

            loan.setOutstandingBalance(Math.round((loan.getOutstandingBalance() - emiToPay) * 100.0) / 100.0);
            if (loan.getOutstandingBalance() <= 0) {
                loan.setStatus(LoanStatus.PAID);
            }
            loan.setLastPaymentAt(now);
            loanRepository.save(loan);

            
            Transaction transaction = Transaction.builder()
                    .referenceNumber("TXN" + System.currentTimeMillis())
                    .fromAccount(payAccount)
                    .toAccount(null)
                    .amount(emiLong)
                    .transactionType(TransactionType.DEBIT)
                    .transactionMode(TransactionMode.WITHDRAWAL)
                    .status(TransactionStatus.SUCCESS)
                    .remarks("Autopay EMI Debit for " + loan.getLoanType() + " Loan (ID: " + loan.getId() + ")")
                    .build();
            transactionRepository.save(transaction);

            AccountLedger ledger = AccountLedger.builder()
                    .amount(emiLong)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(payAccount.getBalance())
                    .ledgerType(LedgerType.DEBIT)
                    .account(payAccount)
                    .transaction(transaction)
                    .build();
            accountLedgerRepository.save(ledger);
        }
    }
}
