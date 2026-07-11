package com.sk.SpringSecurity.service;

import java.util.List;
import com.sk.SpringSecurity.dtos.LoanApplicationRequestDTO;
import com.sk.SpringSecurity.dtos.LoanResponseDTO;

public interface LoanService {
    LoanResponseDTO applyForLoan(LoanApplicationRequestDTO request);
    List<LoanResponseDTO> getMyLoans();
    String payEmi(Long loanId, Long bankAccountId);
    List<LoanResponseDTO> getAllLoans();
    String updateLoanStatus(Long loanId, String status);
}
