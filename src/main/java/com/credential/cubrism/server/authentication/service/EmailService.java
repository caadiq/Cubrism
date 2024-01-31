package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.utils.RedisUtil;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    private final RedisUtil redisUtil;

    private final JavaMailSender mailSender;
    private int verificationCode;

    @Autowired
    public EmailService(JavaMailSender mailSender, RedisUtil redisUtil) {
        this.mailSender = mailSender;
        this.redisUtil = redisUtil;
    }

    public void createVerificationCode() {
        Random random = new Random();
        verificationCode = random.nextInt(900000) + 100000;
    }

    // MimeMessage 생성
    public MimeMessage createEmail(String receiver) throws Exception {
        createVerificationCode();

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

    // 인증 코드 이메일 전송
    public void sendEmail(String receiver) throws Exception {
        MimeMessage message = createEmail(receiver);
        try {
            // 이메일 전송 전 redis에 해당 이메일로 저장된 인증 코드가 있는지 확인 후 있다면 삭제
            if (redisUtil.getData(receiver) != null) {
                redisUtil.deleteData(receiver);
            }

            mailSender.send(message); // 이메일 전송
            redisUtil.setDataExpire(receiver, Integer.toString(verificationCode), 300); // redis(로컬)에 이메일을 키로 갖는 인증 코드 5분 동안 저장
        } catch (MailException e) {
            throw new IllegalArgumentException("이메일 전송에 실패했습니다.");
        }
    }

    // 인증 코드 확인
    public void verifyCode(String email, String authNum) {
        String storedAuthNum = redisUtil.getData(email);
        if (storedAuthNum == null || !storedAuthNum.equals(authNum)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        } else {
            redisUtil.deleteData(email);
        }
    }
}