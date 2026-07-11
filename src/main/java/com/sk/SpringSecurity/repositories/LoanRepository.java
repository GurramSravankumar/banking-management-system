package com.sk.SpringSecurity.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sk.SpringSecurity.Entity.Loan;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.enums.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);
    List<Loan> findByStatus(LoanStatus status);
}
