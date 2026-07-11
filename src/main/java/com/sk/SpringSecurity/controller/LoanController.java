package com.sk.SpringSecurity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.SpringSecurity.dtos.LoanApplicationRequestDTO;
import com.sk.SpringSecurity.dtos.LoanResponseDTO;
import com.sk.SpringSecurity.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoanController {

    private final LoanService loanService;

    

    @PostMapping("/user/loans/apply")
    public ResponseEntity<LoanResponseDTO> applyForLoan(
            @RequestBody LoanApplicationRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(loanService.applyForLoan(request));
    }

    @GetMapping("/user/loans")
    public ResponseEntity<List<LoanResponseDTO>> getMyLoans() {
        return ResponseEntity
                .ok(loanService.getMyLoans());
    }

    @PostMapping("/user/loans/{loanId}/pay-emi")
    public ResponseEntity<String> payEmi(
            @PathVariable Long loanId,
            @RequestParam Long accountId) {
        return ResponseEntity
                .ok(loanService.payEmi(loanId, accountId));
    }

    

    @GetMapping("/admin/loans")
    public ResponseEntity<List<LoanResponseDTO>> getAllLoans() {
        return ResponseEntity
                .ok(loanService.getAllLoans());
    }

    @PostMapping("/admin/loans/{loanId}/status")
    public ResponseEntity<String> updateLoanStatus(
            @PathVariable Long loanId,
            @RequestParam String status) {
        return ResponseEntity
                .ok(loanService.updateLoanStatus(loanId, status));
    }
}
