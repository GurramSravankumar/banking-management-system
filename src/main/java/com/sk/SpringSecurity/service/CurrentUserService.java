package com.sk.SpringSecurity.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.repositories.UserRepository;
import com.sk.SpringSecurity.security.AppUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails userDetails)) {
            throw new RuntimeException("Authenticated user not found");
        }

        return userDetails.getUserId();
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getCurrentUserWithVerification() {
        return userRepository.findWithVerificationById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
