package com.sk.SpringSecurity.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sk.SpringSecurity.enums.TransactionMode;
import com.sk.SpringSecurity.enums.TransactionStatus;
import com.sk.SpringSecurity.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(nullable = false, unique = true, length = 30)
    private String referenceNumber;

    @Column(name = "idempotency_key", unique = true, length = 64)
    private String idempotencyKey;

    
    @JsonIgnoreProperties({"sentTransactions", "receivedTransactions", "user", "ledgerEntries", "hibernateLazyInitializer", "handler"})
    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private BankAccount fromAccount;

    
    @JsonIgnoreProperties({"sentTransactions", "receivedTransactions", "user", "ledgerEntries", "hibernateLazyInitializer", "handler"})
    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private BankAccount toAccount;

    
    @Column(nullable = false)
    private Long amount;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionMode transactionMode;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    
    @Column(length = 200)
    private String remarks;

    
    @CreationTimestamp
    private LocalDateTime transactionTime;

    
    @JsonIgnore
    @OneToMany(mappedBy = "transaction")
    private List<AccountLedger> ledgerEntries = new ArrayList<>();

}
