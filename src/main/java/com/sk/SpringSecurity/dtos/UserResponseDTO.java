package com.sk.SpringSecurity.dtos;

import java.time.LocalDateTime;

import com.sk.SpringSecurity.enums.Role;
import com.sk.SpringSecurity.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private Role role;

    private UserStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}