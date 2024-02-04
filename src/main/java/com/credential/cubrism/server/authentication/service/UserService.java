package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.dto.UsersDTO;
import com.credential.cubrism.server.authentication.model.Users;
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
    public void signUp(UsersDTO usersDto) {
        // Internal Server Error 테스트용
        // internalserver@error로 회원가입 시도하면 RuntimeException 발생
        if (usersDto.getEmail().equals("internalserver@error.com")) {
            throw new RuntimeException("내부 서버 오류 테스트");
        }

        Users users = new Users();
        users.setEmail(usersDto.getEmail());
        users.setPassword(bCryptPasswordEncoder.encode(usersDto.getPassword()));
        users.setNickname(usersDto.getNickname());
        userRepository.save(users);
    }
}