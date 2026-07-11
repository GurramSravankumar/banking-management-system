package com.sk.SpringSecurity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.SpringSecurity.Entity.Beneficiary;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.dtos.BeneficiaryResponseDTO;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long>{

	boolean existsByUserAndAccountNumber(User user, String accountNumber);
	
	List<Beneficiary> findByUserAndActiveTrue(User user);
	
	Optional<Beneficiary> findByIdAndUserAndActiveTrue(Long id, User user);
	
	Optional<Beneficiary> findByUserAndAccountNumber( User user,String accountNumber);
	

}
