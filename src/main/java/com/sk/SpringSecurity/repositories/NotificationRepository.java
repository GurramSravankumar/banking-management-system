package com.sk.SpringSecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.SpringSecurity.Entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
