package com.credential.cubrism.server.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {
    @Value("${mail.google.email}")
    private String senderEmail;

    private final JavaMailSender mailSender;
    private int authNumber;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void makeAuthNumber() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber.toString());
    }

    public String emailFormat(String emailTo) {
        makeAuthNumber();

        String emailFrom = senderEmail;
        String title = "[Cubrism] 회원 가입 인증 메일입니다.";
        String content = "인증 번호는 " + authNumber + "입니다.";
        sendEmail(emailFrom, emailTo, title, content);

        return Integer.toString(authNumber);
    }

    public void sendEmail(String emailFrom, String emailTo, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailFrom);
            helper.setTo(emailTo);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}