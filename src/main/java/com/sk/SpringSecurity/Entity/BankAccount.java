package com.sk.SpringSecurity.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sk.SpringSecurity.enums.AccountStatus;
import com.sk.SpringSecurity.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

 
    @Column(nullable = false)
    private Long balance = 0L;

    
    @Column(nullable = false, length = 11)
    private String ifscCode;

   
    @Column(nullable = false, length = 100)
    private String branchName;

   
    @Column(nullable = false, length = 10)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus;

    
    @CreationTimestamp
    private LocalDateTime openedAt;

   
    private LocalDateTime closedAt;


    @JsonIgnoreProperties({"accounts", "verification", "beneficiaries", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> sentTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> receivedTransactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account")
    private List<AccountLedger> ledgerEntries = new ArrayList<>();
}