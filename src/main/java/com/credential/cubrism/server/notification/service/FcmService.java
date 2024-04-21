package com.credential.cubrism.server.notification.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.notification.dto.FcmTokenDto;
import com.credential.cubrism.server.notification.dto.PushMessageDto;
import com.credential.cubrism.server.notification.entity.FcmTokens;
import com.credential.cubrism.server.notification.repository.FcmRepository;
import com.credential.cubrism.server.notification.utils.FcmUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmService {
    private final FcmRepository fcmRepository;

    private final SecurityUtil securityUtil;
    private final FcmUtils fcmUtils;

    public ResponseEntity<MessageDto> pushMessage(PushMessageDto dto) {
        try {
            fcmUtils.sendMessageTo(dto.getTargetToken(), dto.getTitle(), dto.getBody());
            return ResponseEntity.ok().body(new MessageDto("알림을 전송했습니다."));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FCM_SEND_FAILURE);
        }
    }

    @Transactional
    public ResponseEntity<MessageDto> updateToken(FcmTokenDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        FcmTokens fcmTokens = new FcmTokens();
        fcmTokens.setUserId(currentUser.getUuid());
        fcmTokens.setToken(dto.getFcmToken());
        fcmRepository.save(fcmTokens);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("FCM 토큰이 업데이트되었습니다."));
    }
}
