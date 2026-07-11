package com.sk.SpringSecurity.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sk.SpringSecurity.dtos.LoginRequestDTO;
import com.sk.SpringSecurity.dtos.LoginResponseDTO;
import com.sk.SpringSecurity.dtos.RegisterRequestDTO;
import com.sk.SpringSecurity.Entity.UserVerification;
import com.sk.SpringSecurity.dtos.RegisterResponseDTO;
import com.sk.SpringSecurity.enums.KycStatus;
import com.sk.SpringSecurity.repositories.UserVerificationRepository;
import com.sk.SpringSecurity.enums.Role;
import com.sk.SpringSecurity.enums.UserStatus;
import com.sk.SpringSecurity.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import com.sk.SpringSecurity.Entity.User;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final UserVerificationRepository userVerificationRepository;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;

        @Override
        public RegisterResponseDTO register(RegisterRequestDTO request) {

                if (userRepository.existsByEmail(request.getEmail())) {
                        return RegisterResponseDTO.builder()
                                        .message("Email already exists.")
                                        .email(request.getEmail())
                                        .status("FAILED")
                                        .build();
                }

                if (userRepository.existsByPhone(request.getPhone())) {
                        return RegisterResponseDTO.builder()
                                        .message("Phone number already exists.")
                                        .email(request.getEmail())
                                        .status("FAILED")
                                        .build();
                }

                User user = User.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .phone(request.getPhone())
                                .aadhaarNumber(request.getAadhaarNumber())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .dateOfBirth(request.getDateOfBirth())
                                .gender(request.getGender())
                                .occupation(request.getOccupation())
                                .annualIncome(request.getAnnualIncome())
                                .nationality(request.getNationality())
                                .houseNo(request.getHouseNo())
                                .street(request.getStreet())
                                .city(request.getCity())
                                .district(request.getDistrict())
                                .state(request.getState())
                                .country(request.getCountry())
                                .pincode(request.getPincode())
                                .role(Role.USER)
                                .status(UserStatus.PENDING)
                                .build();

                user = userRepository.save(user);

                UserVerification verification = UserVerification.builder()
                                .user(user)
                                .emailVerified(true)
                                .mobileVerified(true)
                                .aadhaarVerified(false)
                                .panVerified(false)
                                .kycStatus(KycStatus.PENDING)
                                .build();

                userVerificationRepository.save(verification);

                return RegisterResponseDTO.builder()
                                .message("Registration successful. Please login and complete your KYC.")
                                .email(user.getEmail())
                                .status(user.getStatus().name())
                                .build();
        }

        @Override
        public LoginResponseDTO login(LoginRequestDTO request) {

                try {

                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));

                } catch (BadCredentialsException ex) {

                        return LoginResponseDTO.builder()
                                        .message("Invalid email or Password")
                                        .build();
                }

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow();

                UserVerification verification = userVerificationRepository
                                .findByUser(user)
                                .orElseThrow(() -> new RuntimeException("User verification not found"));

                String token = jwtService.generateToken(user);

                return LoginResponseDTO.builder()
                                .message("Login Successful")
                                .token(token)
                                .role(user.getRole().name())
                                .status(user.getStatus().name())
                                .kycStatus(verification.getKycStatus().name())
                                .customerId(user.getCustomerId())
                                .build();
        }
}