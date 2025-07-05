// File: com.xinpay.backend.service.EmailService.java
package com.xinpay.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "🔐 XinPay - OTP Verification Code";
        String body = "Dear User,\n\n"
                + "Your One-Time Password (OTP) for XinPay is: " + otp + "\n\n"
                + "⚠️ Please do not share this OTP with anyone.\n"
                + "It is valid for a limited time and is required to complete your verification.\n\n"
                + "Thank you,\n"
                + "Team XinPay";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
