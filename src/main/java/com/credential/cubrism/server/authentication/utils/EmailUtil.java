package com.credential.cubrism.server.authentication.utils;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;

    public int sendEmail(String receiver) throws Exception {
        int verificationCode = createVerificationCode();
        MimeMessage message = createEmail(receiver, verificationCode);
        mailSender.send(message);
        return verificationCode;
    }

    private int createVerificationCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    private MimeMessage createEmail(String receiver, int verificationCode) throws Exception {
        // 메일 제목
        String title = "[Cubrism] 이메일 인증 코드";
        // 메일 내용 (html 형식)
        String content =
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<style>" +
                        "  .email-container {" +
                        "    font-family: 'Arial', sans-serif;" +
                        "    text-align: center;" +
                        "    padding: 50px;" +
                        "  }" +
                        "  h1 {" +
                        "    color: #7796E8;" +
                        "  }" +
                        "  p {" +
                        "    color: #808080;" +
                        "  }" +
                        "  .verification-code {" +
                        "    font-size: 24px;" +
                        "    color: #000000;" +
                        "    border: 1px solid #808080;" +
                        "    border-radius: 5px;" +
                        "    display: inline-block;" +
                        "    padding: 10px 20px;" +
                        "    font-weight: bold;" +
                        "    margin-top: 20px;" +
                        "  }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='email-container'>" +
                        "  <h1>Cubrism에 오신 것을 환영합니다!</h1>" +
                        "  <p>아래 인증 번호를 입력하여 회원가입을 진행해 주세요.</p>" +
                        "  <div class='verification-code'>" + verificationCode + "</div>" +
                        "  <p>인증번호는 5분간 유효합니다.</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setTo(receiver);
        helper.setSubject(title);
        helper.setText(content, true);
        return message;
    }
}
