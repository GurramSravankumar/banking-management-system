package com.sk.SpringSecurity.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {

    
    private String customerId;
    private String fullName;
    private String email;
    private String phone;
    private String userStatus;

    
    private LocalDate dateOfBirth;
    private String gender;
    private String occupation;
    private Long annualIncome;
    private String nationality;

    
    private String houseNo;
    private String street;
    private String city;
    private String district;
    private String state;
    private String country;
    private String pincode;

    
    private String kycStatus;
    private boolean emailVerified;
    private boolean mobileVerified;
    private boolean aadhaarVerified;
    private boolean panVerified;

    
    private String profilePhotoPath;
}
