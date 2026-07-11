package com.sk.SpringSecurity.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.SpringSecurity.Entity.AdminAuditLog;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    List<AdminAuditLog> findAllByOrderByCreatedAtDesc();
}
