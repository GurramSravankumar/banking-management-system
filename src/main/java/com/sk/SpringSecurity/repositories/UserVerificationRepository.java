package com.sk.SpringSecurity.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.Entity.UserVerification;
import com.sk.SpringSecurity.enums.KycStatus;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {

	Optional<UserVerification> findByUser(User user);

	List<UserVerification> findByKycStatus(KycStatus kycStatus);

	@Query("SELECT uv FROM UserVerification uv WHERE uv.kycStatus != com.sk.SpringSecurity.enums.KycStatus.PENDING AND uv.user.status = com.sk.SpringSecurity.enums.UserStatus.PENDING")
	List<UserVerification> findPendingKycRequests();

}