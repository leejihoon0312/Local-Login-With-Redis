package com.deadline826.bedi.login.Service;

import com.deadline826.bedi.Goal.Domain.Goal;
import com.deadline826.bedi.Token.Domain.RefreshToken;
import com.deadline826.bedi.exception.DuplicateEmailException;
import com.deadline826.bedi.login.Domain.User;
import com.deadline826.bedi.Token.Domain.Dto.TokenDto;
import com.deadline826.bedi.login.Domain.Dto.UserDto;
import com.deadline826.bedi.security.CustomAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

public interface UserService {

    void setCustomAuthenticationFilter(CustomAuthenticationFilter authenticationFilter); //의존성 주입

    void saveUser(UserDto dto) throws DuplicateEmailException;   //유저정보 저장

    TokenDto login(UserDto dto);

    void updateRefreshToken(String id, RefreshToken refreshToken);  //RefreshToken 업데이트

    TokenDto refresh(String refreshToken);  //RefreshToken 으로 AccessToken 받아올때 사용

    UserDetails loadUserById(Long id);

    User findUserById(Long id);

    User findUserByEmail(String email);

    UserDetails loadUserByEmail(String email) throws UsernameNotFoundException;

    User getUserFromAccessToken();  //토큰에서 회원 객체 추출

}
