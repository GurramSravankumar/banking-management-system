package com.sk.SpringSecurity.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sk.SpringSecurity.Entity.Notification;
import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.enums.NotificationChannel;
import com.sk.SpringSecurity.enums.NotificationStatus;
import com.sk.SpringSecurity.repositories.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendEmail(User user, String subject, String message) {
        saveNotification(user, NotificationChannel.EMAIL, user.getEmail(), subject, message);
    }

    @Override
    public void sendSms(User user, String message) {
        saveNotification(user, NotificationChannel.SMS, user.getPhone(), "Bank Alert", message);
    }

    @Override
    public void sendTransactionAlerts(User user, String message) {
        sendEmail(user, "Transaction Alert", message);
        sendSms(user, message);
    }

    private void saveNotification(
            User user,
            NotificationChannel channel,
            String recipient,
            String subject,
            String message) {

        Notification notification = Notification.builder()
                .user(user)
                .channel(channel)
                .recipient(recipient)
                .subject(subject)
                .message(message)
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
}
