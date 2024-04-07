package com.credential.cubrism.server.authentication.utils;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    @Value("${rest.api.url}")
    private String restApiUrl;

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    public int sendVerifyEmail(String receiver) throws Exception {
        int verificationCode = createVerificationCode();
        MimeMessage message = createVerifyEmail(receiver, verificationCode);
        mailSender.send(message);
        return verificationCode;
    }

    public void sendResetPasswordEmail(String receiver, String uuid) throws Exception {
        MimeMessage message = createResetPasswordEmail(receiver, uuid);
        mailSender.send(message);
    }

    private int createVerificationCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    // resources 폴더에 있는 html 파일을 읽어옴
    private String loadEmailTemplate(String templatePath, Map<String, String> replacements) throws IOException {
        Resource resource = resourceLoader.getResource(templatePath);
        InputStream inputStream = resource.getInputStream();
        String content = StreamUtils.copyToString(inputStream, Charset.defaultCharset());

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return content;
    }

    private MimeMessage createMimeMessage(String receiver, String title, String content) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setTo(receiver);
        helper.setSubject(title);
        helper.setText(content, true);
        return message;
    }

    // 이메일 인증 코드 발송 이메일
    private MimeMessage createVerifyEmail(String receiver, int verificationCode) throws Exception {
        // 메일 제목
        String title = "[Cubrism] 이메일 인증 코드";
        Map<String, String> replacements = new HashMap<>();
        replacements.put("verificationCode", String.valueOf(verificationCode));
        // 메일 내용
        String content = loadEmailTemplate("classpath:email_verify_code.html", replacements);

        return createMimeMessage(receiver, title, content);
    }

    // 비밀번호 재설정 이메일
    private MimeMessage createResetPasswordEmail(String receiver, String uuid) throws Exception {
        // 메일 제목
        String title = "[Cubrism] 이메일 인증 코드";
        Map<String, String> replacements = new HashMap<>();
        replacements.put("resetPasswordUrl", restApiUrl + "/auth/users/password/reset/" + uuid);
        // 메일 내용
        String content = loadEmailTemplate("classpath:email_reset_password.html", replacements);

        return createMimeMessage(receiver, title, content);
    }
}
