package com.sk.SpringSecurity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Transaction;

public interface TransactionRepository extends  JpaRepository<Transaction, Long> {

	List<Transaction> findByFromAccountInOrToAccountInOrderByTransactionTimeDesc(
	        List<BankAccount> fromAccounts,
	        List<BankAccount> toAccounts);
	Optional<Transaction> findByReferenceNumber(String referenceNumber);
	Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    @Query("""
            select distinct t from Transaction t
            join fetch t.fromAccount fa
            join fetch fa.user
            join fetch t.toAccount ta
            join fetch ta.user
            where fa.user.id = :userId or ta.user.id = :userId
            order by t.transactionTime desc
            """)
    List<Transaction> findUserTransactions(@Param("userId") Long userId);

    @Query("""
            select t from Transaction t
            join fetch t.fromAccount fa
            join fetch fa.user
            join fetch t.toAccount ta
            join fetch ta.user
            where t.referenceNumber = :referenceNumber
            """)
    Optional<Transaction> findReceiptTransaction(@Param("referenceNumber") String referenceNumber);
}
