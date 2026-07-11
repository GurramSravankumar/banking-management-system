package com.sk.SpringSecurity.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.SpringSecurity.Entity.AccountLedger;
import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Loan;
import com.sk.SpringSecurity.Entity.Transaction;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.dtos.LoanApplicationRequestDTO;
import com.sk.SpringSecurity.dtos.LoanResponseDTO;
import com.sk.SpringSecurity.enums.AccountStatus;
import com.sk.SpringSecurity.enums.LedgerType;
import com.sk.SpringSecurity.enums.LoanStatus;
import com.sk.SpringSecurity.enums.LoanType;
import com.sk.SpringSecurity.enums.TransactionMode;
import com.sk.SpringSecurity.enums.TransactionStatus;
import com.sk.SpringSecurity.enums.TransactionType;
import com.sk.SpringSecurity.enums.UserStatus;
import com.sk.SpringSecurity.repositories.AccountLedgerRepository;
import com.sk.SpringSecurity.repositories.AccountRepository;
import com.sk.SpringSecurity.repositories.LoanRepository;
import com.sk.SpringSecurity.repositories.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final CurrentUserService currentUserService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountLedgerRepository accountLedgerRepository;

    @Override
    @Transactional
    public LoanResponseDTO applyForLoan(LoanApplicationRequestDTO request) {
        User user = currentUserService.getCurrentUser();
        if (user.getStatus() != UserStatus.APPROVED) {
            throw new RuntimeException("Only approved users can apply for loans.");
        }

        LoanType type;
        try {
            type = LoanType.valueOf(request.getLoanType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Loan Type: " + request.getLoanType());
        }

        double interestRate = getInterestRateForType(type);
        double amount = request.getAmount();
        int termMonths = request.getTermMonths();

        
        
        double monthlyRate = (interestRate / 12.0) / 100.0;
        double emi = (amount * monthlyRate * Math.pow(1 + monthlyRate, termMonths)) 
                     / (Math.pow(1 + monthlyRate, termMonths) - 1);
        
        
        if (monthlyRate == 0) {
            emi = amount / termMonths;
        }

        
        emi = Math.round(emi * 100.0) / 100.0;

        Loan loan = Loan.builder()
                .user(user)
                .amount(amount)
                .interestRate(interestRate)
                .termMonths(termMonths)
                .emiAmount(emi)
                .outstandingBalance(amount)
                .loanType(type)
                .status(LoanStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Loan saved = loanRepository.save(loan);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getMyLoans() {
        User user = currentUserService.getCurrentUser();
        List<Loan> loans = loanRepository.findByUser(user);
        List<LoanResponseDTO> dtos = new ArrayList<>();
        for (Loan l : loans) {
            dtos.add(mapToDTO(l));
        }
        return dtos;
    }


    @Override
    @Transactional
    public String payEmi(Long loanId, Long bankAccountId) {
        User user = currentUserService.getCurrentUser();
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized loan repayment access.");
        }

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new RuntimeException("Cannot pay EMI for loan with status: " + loan.getStatus());
        }

        BankAccount account = accountRepository.findWithUserById(bankAccountId)
                .orElseThrow(() -> new RuntimeException("Bank Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized bank account access.");
        }

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Selected bank account is not active.");
        }

        double emiToPay = loan.getEmiAmount();
        
        if (loan.getOutstandingBalance() < emiToPay) {
            emiToPay = loan.getOutstandingBalance();
        }

        long emiLong = Math.round(emiToPay);
        if (account.getBalance() < emiLong) {
            throw new RuntimeException("Insufficient balance in bank account to pay EMI (Required: Rs. " + emiLong + ")");
        }

        long balanceBefore = account.getBalance();
        account.setBalance(balanceBefore - emiLong);
        accountRepository.save(account);

        loan.setOutstandingBalance(Math.round((loan.getOutstandingBalance() - emiToPay) * 100.0) / 100.0);
        if (loan.getOutstandingBalance() <= 0) {
            loan.setStatus(LoanStatus.PAID);
        }
        loanRepository.save(loan);

        
        Transaction transaction = Transaction.builder()
                .referenceNumber("TXN" + System.currentTimeMillis())
                .fromAccount(account)
                .toAccount(null)
                .amount(emiLong)
                .transactionType(TransactionType.DEBIT)
                .transactionMode(TransactionMode.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .remarks("EMI Repayment for " + loan.getLoanType() + " Loan (ID: " + loan.getId() + ")")
                .build();
        transactionRepository.save(transaction);

        AccountLedger ledger = AccountLedger.builder()
                .amount(emiLong)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getBalance())
                .ledgerType(LedgerType.DEBIT)
                .account(account)
                .transaction(transaction)
                .build();
        accountLedgerRepository.save(ledger);

        return "EMI of Rs. " + emiLong + " paid successfully. Remaining loan outstanding: Rs. " + loan.getOutstandingBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        List<LoanResponseDTO> dtos = new ArrayList<>();
        for (Loan l : loans) {
            dtos.add(mapToDTO(l));
        }
        return dtos;
    }


    @Override
    @Transactional
    public String updateLoanStatus(Long loanId, String statusString) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Can only update status of PENDING loans.");
        }

        LoanStatus newStatus;
        try {
            newStatus = LoanStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Loan Status: " + statusString);
        }

        if (newStatus == LoanStatus.APPROVED) {
            User debtor = loan.getUser();
            
            List<BankAccount> debtorAccounts = accountRepository.findByUserId(debtor.getId());
            BankAccount disbursementAccount = debtorAccounts.stream()
                    .filter(a -> a.getAccountStatus() == AccountStatus.ACTIVE)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Debtor has no active bank account for loan disbursal. Please open an account first."));

            long balanceBefore = disbursementAccount.getBalance();
            long loanPrincipalLong = Math.round(loan.getAmount());
            disbursementAccount.setBalance(balanceBefore + loanPrincipalLong);
            accountRepository.save(disbursementAccount);

            loan.setStatus(LoanStatus.APPROVED);
            loan.setApprovedAt(LocalDateTime.now());
            loanRepository.save(loan);

            
            Transaction transaction = Transaction.builder()
                    .referenceNumber("TXN" + System.currentTimeMillis())
                    .fromAccount(null)
                    .toAccount(disbursementAccount)
                    .amount(loanPrincipalLong)
                    .transactionType(TransactionType.CREDIT)
                    .transactionMode(TransactionMode.DEPOSIT)
                    .status(TransactionStatus.SUCCESS)
                    .remarks("Loan Disbursal: " + loan.getLoanType() + " Loan (ID: " + loan.getId() + ")")
                    .build();
            transactionRepository.save(transaction);

            AccountLedger ledger = AccountLedger.builder()
                    .amount(loanPrincipalLong)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(disbursementAccount.getBalance())
                    .ledgerType(LedgerType.CREDIT)
                    .account(disbursementAccount)
                    .transaction(transaction)
                    .build();
            accountLedgerRepository.save(ledger);

            return "Loan APPROVED. Principal of Rs. " + loanPrincipalLong + " disbursed to account " + disbursementAccount.getAccountNumber();
        } else if (newStatus == LoanStatus.REJECTED) {
            loan.setStatus(LoanStatus.REJECTED);
            loanRepository.save(loan);
            return "Loan application REJECTED.";
        } else {
            throw new RuntimeException("Invalid target transition status from PENDING: " + newStatus);
        }
    }

    private double getInterestRateForType(LoanType type) {
        switch (type) {
            case HOME:
                return 8.5;
            case CAR:
                return 10.0;
            case EDUCATION:
                return 9.0;
            case PERSONAL:
            default:
                return 12.0;
        }
    }

    private LoanResponseDTO mapToDTO(Loan loan) {
        return LoanResponseDTO.builder()
                .loanId(loan.getId())
                .amount(loan.getAmount())
                .interestRate(loan.getInterestRate())
                .termMonths(loan.getTermMonths())
                .emiAmount(loan.getEmiAmount())
                .outstandingBalance(loan.getOutstandingBalance())
                .loanType(loan.getLoanType().name())
                .status(loan.getStatus().name())
                .applicantName(loan.getUser().getFullName())
                .applicantEmail(loan.getUser().getEmail())
                .createdAt(loan.getCreatedAt())
                .approvedAt(loan.getApprovedAt())
                .build();
    }
}
