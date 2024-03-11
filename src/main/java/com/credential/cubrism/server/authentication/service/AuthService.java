package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.dto.SignInPostDTO;
import com.credential.cubrism.server.authentication.dto.SignUpPostDTO;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public void signUp(SignUpPostDTO dto) {
        Users users = new Users();
        users.setEmail(dto.getEmail());
        users.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        users.setNickname(dto.getNickname());
        userRepository.save(users);
    }

    /**
     * 로그인 기능
     * 화면에서 LoginRequest(loginId, password)을 입력받아 loginId와 password가 일치하면 User return
     * loginId가 존재하지 않거나 password가 일치하지 않으면 null return
     */
    public Users signIn(SignInPostDTO dto) {
        Optional<Users> optionalUser = userRepository.findByEmail(dto.getEmail());

        // loginId와 일치하는 User가 없으면 null return
        if (optionalUser.isEmpty()) {
            return null;
        }

        Users user = optionalUser.get();

        // 찾아온 User의 password와 입력된 password가 다르면 null return
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return null;
        }

        return user;
    }

    /**
     * loginId(String)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * loginId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * loginId로 찾아온 User가 존재하면 User return
     */
    public Users getUserByEmail(String email) {
        if (email == null) return null;

        Optional<Users> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    public Users getUserByUuid(UUID uuid) {
        if (uuid == null) return null;

        Optional<Users> optionalUser = userRepository.findByUuid(uuid);
        return optionalUser.orElse(null);
    }
}