package com.sk.SpringSecurity.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequestDTO {

    private String fullName;
    private String phone;
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
    private LocalDate dateOfBirth;
    private String gender;
}
