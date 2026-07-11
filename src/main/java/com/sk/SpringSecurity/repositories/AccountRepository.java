package com.sk.SpringSecurity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;

import com.sk.SpringSecurity.Entity.BankAccount;
import com.sk.SpringSecurity.Entity.User;

public interface AccountRepository extends JpaRepository<BankAccount, Long> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    @EntityGraph(attributePaths = "user")
    Optional<BankAccount> findWithUserById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "user")
    @Query("select a from BankAccount a where a.id = :id")
    Optional<BankAccount> findAndLockWithUserById(@Param("id") Long id);

    @EntityGraph(attributePaths = "user")
    Optional<BankAccount> findWithUserByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "user")
    @Query("select a from BankAccount a where a.accountNumber = :accountNumber")
    Optional<BankAccount> findAndLockWithUserByAccountNumber(@Param("accountNumber") String accountNumber);

    List<BankAccount> findByUser(User user);

    List<BankAccount> findByUserId(Long userId);
}
