package com.deadline826.bedi.security;

import com.deadline826.bedi.login.Domain.User;
import com.deadline826.bedi.login.Service.VerifyPasswordService;
import com.deadline826.bedi.login.repository.UserRepository;
import com.deadline826.bedi.login.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final VerifyPasswordService verifyPasswordService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getPrincipal().toString();

        // 이메일로 유저정보 불러오기
        UserDetails userDetails = userService.loadUserByEmail(email);

        // redis 에 저장된 값을 key(email) 를 이용해 가져온다
        String password = verifyPasswordService.getTempPassword(email);


        // PW 검사
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Provider - authenticate() : 비밀번호가 일치하지 않습니다.");
        }

        // redis 에 저장된 값을 key(email) 를 이용해 삭제한다
        verifyPasswordService.removeTempPassword(email);

        // Collections.EMPTY_LIST 를 통해 비밀번호 없이 인증가능하게 함
        //CustomAuthenticationFilter 의 attemptAuthentication 으로 복귀
        return new UsernamePasswordAuthenticationToken(email, null, Collections.EMPTY_LIST);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}