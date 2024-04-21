package com.credential.cubrism.server.notification.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.notification.dto.FcmTokenDto;
import com.credential.cubrism.server.notification.dto.PushMessageDto;
import com.credential.cubrism.server.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmController {
    private final FcmService fcmService;

    @PostMapping("/fcm")
    public ResponseEntity<MessageDto> pushMessage(@RequestBody PushMessageDto dto) {
        return fcmService.pushMessage(dto);
    }

    @PutMapping("/fcm")
    public ResponseEntity<MessageDto> updateToken(@RequestBody FcmTokenDto dto) {
        return fcmService.updateToken(dto);
    }
}
