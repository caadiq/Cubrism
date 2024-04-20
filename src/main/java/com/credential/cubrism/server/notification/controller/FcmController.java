package com.credential.cubrism.server.notification.controller;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.notification.dto.FcmTokenRequest;
import com.credential.cubrism.server.notification.service.FirebaseCloudMessageService;
import com.credential.cubrism.server.notification.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FcmController {
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final UserRepository userRepository;

    @PostMapping("/api/fcm")
    public ResponseEntity pushMessage(@RequestBody RequestDto requestDTO) throws IOException {
        System.out.println(requestDTO.getTargetToken() + " "
                +requestDTO.getTitle() + " " + requestDTO.getBody());

        firebaseCloudMessageService.sendMessageTo(
                requestDTO.getTargetToken(),
                requestDTO.getTitle(),
                requestDTO.getBody());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/fcmtoken/{email}")
    public ResponseEntity<Void> updateFcmToken(@PathVariable String email, @RequestBody FcmTokenRequest request) {
        System.out.println("FCM 토큰 업데이트 요청 들어옴");
        // 이메일에 해당하는 사용자를 찾습니다.
        Optional<Users> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            // 이메일에 해당하는 사용자가 없으면 404 Not Found 응답을 전송합니다.
            return ResponseEntity.notFound().build();
        }

        Optional<Users> otherUserOptional = userRepository.findByFcmToken(request.getFcmToken());
        if (otherUserOptional.isPresent()) {
            // 동일한 FCM 토큰을 가진 다른 사용자가 있다면, 그 사용자의 FCM 토큰을 삭제합니다.
            Users otherUser = otherUserOptional.get();
            otherUser.setFcmToken(null);
            userRepository.save(otherUser);
        }

        // 사용자가 있으면 FCM 토큰을 업데이트합니다.
        Users user = userOptional.get();

        // 사용자가 있으면 FCM 토큰을 업데이트합니다.
        user.setFcmToken(request.getFcmToken());
        System.out.println("FCM 토큰 업데이트: " + request.getFcmToken());
        userRepository.save(user);

        // 업데이트가 성공적으로 완료되면, 200 OK 응답을 전송합니다.
        return ResponseEntity.ok().build();
    }
}
