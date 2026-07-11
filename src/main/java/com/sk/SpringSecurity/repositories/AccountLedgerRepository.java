package com.sk.SpringSecurity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sk.SpringSecurity.Entity.AccountLedger;
import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.Transaction;

public interface AccountLedgerRepository extends JpaRepository<AccountLedger,Long> {
	Optional<AccountLedger> findByTransactionAndAccount(
	        Transaction transaction,
	        BankAccount account);
	List<AccountLedger> findByAccountOrderByCreatedAtDesc(BankAccount account);

    @Query("""
            select l from AccountLedger l
            join fetch l.transaction t
            where l.account.id = :accountId
            order by l.createdAt desc
            """)
    List<AccountLedger> findStatementByAccountId(@Param("accountId") Long accountId);
}
