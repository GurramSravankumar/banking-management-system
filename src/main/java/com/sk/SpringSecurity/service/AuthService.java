package com.sk.SpringSecurity.service;

import org.springframework.data.domain.jaxb.SpringDataJaxb.PageRequestDto;

import com.sk.SpringSecurity.dtos.LoginRequestDTO;
import com.sk.SpringSecurity.dtos.LoginResponseDTO;
import com.sk.SpringSecurity.dtos.RegisterRequestDTO;
import com.sk.SpringSecurity.dtos.RegisterResponseDTO;

public interface AuthService {
	
	RegisterResponseDTO register(RegisterRequestDTO request);
	
	LoginResponseDTO login(LoginRequestDTO request);


}
