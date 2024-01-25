package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.dto.UserDTO;
import com.credential.cubrism.server.authentication.model.User;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public void signUp(UserDTO userDto) throws Exception {
        // Internal Server Error 테스트용
        // internalserver@error로 회원가입 시도하면 RuntimeException 발생
        if (userDto.getEmail().equals("internalserver@error.com")) {
            throw new RuntimeException("내부 서버 오류 테스트");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setNickname(userDto.getNickname());
        userRepository.save(user);
    }
}