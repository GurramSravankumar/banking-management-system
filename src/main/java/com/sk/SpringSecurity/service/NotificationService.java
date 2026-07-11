package com.sk.SpringSecurity.service;

import com.sk.SpringSecurity.Entity.User;

public interface NotificationService {

    void sendEmail(User user, String subject, String message);

    void sendSms(User user, String message);

    void sendTransactionAlerts(User user, String message);
}
