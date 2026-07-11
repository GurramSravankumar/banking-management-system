package com.sk.SpringSecurity.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sk.SpringSecurity.enums.KycStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    @Column(unique = true, length = 12)
    private String aadhaarNumber;

    @Column(unique = true, length = 10)
    private String panNumber;
    
    private String aadhaarDocumentPath;
    
    private String panDocumentPath;
    
    private String profilePhotoPath;

    private boolean emailVerified;

    private boolean mobileVerified;

    private boolean aadhaarVerified;

    private boolean panVerified;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private String verifiedBy;

    private LocalDateTime verifiedAt;

    @JsonIgnoreProperties({"verification", "accounts", "beneficiaries"})
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}