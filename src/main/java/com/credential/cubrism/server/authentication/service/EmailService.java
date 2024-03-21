package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.utils.EmailUtil;
import com.credential.cubrism.server.authentication.utils.RedisUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final RedisUtil redisUtil;
    private final EmailUtil emailUtil;

    private static final String EMAIL_VERIFICATION_SUFFIX = "(emailVerification)"; // Redis Key 중복 방지를 위한 접미사

    // 인증번호 이메일 전송
    public ResponseEntity<MessageDto> sendEmail(String receiver) {
        try {
            // 인증번호 이메일 전송 후 Redis에 인증번호 5분 동안 저장
            redisUtil.setData(receiver + EMAIL_VERIFICATION_SUFFIX, Integer.toString(emailUtil.sendEmail(receiver)), 300);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("이메일 전송 완료"));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILURE);
        }
    }

    // 인증번호 확인
    public ResponseEntity<MessageDto> verifyCode(String email, String verifyCode) {
        // Redis에서 인증번호 가져오기
        String storedVerifyCode = redisUtil.getData(email + EMAIL_VERIFICATION_SUFFIX);

        // 인증번호 만료
        if (storedVerifyCode == null) {
            throw new CustomException(ErrorCode.VERIFY_CODE_EXPIRED);
        }

        // 인증번호 불일치
        if (!storedVerifyCode.equals(verifyCode)) {
            throw new CustomException(ErrorCode.INVALID_VERIFY_CODE);
        }

        // 인증 완료 후 Redis에서 인증번호 삭제
        redisUtil.deleteData(email + EMAIL_VERIFICATION_SUFFIX);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("이메일 인증 완료"));
    }
}