package com.sk.SpringSecurity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.enums.UserStatus;

public interface UserRepository extends JpaRepository<User,Long>{
	
	Optional<User> findByPhone(String phone);
	
	boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
    
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "verification")
    Optional<User> findWithVerificationById(Long id);
    
    List<User> findByStatus(UserStatus status);

	boolean existsByCustomerId(String customerId);
}
