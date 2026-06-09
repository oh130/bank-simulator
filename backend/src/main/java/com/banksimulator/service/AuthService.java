package com.banksimulator.service;

import com.banksimulator.dto.request.LoginRequest;
import com.banksimulator.dto.request.SignupRequest;
import com.banksimulator.dto.response.AuthResponse;
import com.banksimulator.entity.User;
import com.banksimulator.exception.DuplicateEmailException;
import com.banksimulator.repository.UserRepository;
import com.banksimulator.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스 - 회원가입, 로그인 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입 처리
     * 1. 이메일 중복 확인
     * 2. 비밀번호 암호화
     * 3. 사용자 저장
     * 4. JWT 토큰 발급
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 비밀번호 BCrypt 암호화 후 사용자 저장
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // JWT 토큰 발급
        String token = tokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getName());
    }

    /**
     * 로그인 처리
     * 1. Spring Security 인증 매니저로 이메일/비밀번호 검증
     * 2. 검증 성공 시 JWT 토큰 발급
     */
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager가 CustomUserDetailsService를 통해 사용자 검증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 인증된 사용자의 이메일로 사용자 정보 조회
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // JWT 토큰 발급
        String token = tokenProvider.generateToken(email);
        return new AuthResponse(token, user.getEmail(), user.getName());
    }
}
