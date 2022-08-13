package com.deadline826.bedi.login.Controller;

import com.deadline826.bedi.Token.Domain.Dto.TokenDto;
import com.deadline826.bedi.exception.DuplicateEmailException;
import com.deadline826.bedi.login.Domain.Dto.UserDto;
import com.deadline826.bedi.login.Domain.Dto.UserRequestDto;
import com.deadline826.bedi.login.Domain.User;
import com.deadline826.bedi.login.Service.GoogleService;
import com.deadline826.bedi.login.Service.KakaoService;
import com.deadline826.bedi.login.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.deadline826.bedi.security.JwtConstants.*;
import static com.deadline826.bedi.security.JwtConstants.RT_HEADER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final GoogleService googleService;
    private final KakaoService kakaoService;

    @GetMapping(value = "/kakao" )
    public ResponseEntity<TokenDto> returnco(@RequestParam String code, HttpServletResponse response,
                                             HttpServletRequest request) throws DuplicateEmailException {
        //카카오 서버로 부터 사용자 정보 받아오기
        String accessToken = kakaoService.getAccessToken(code);
        UserDto userDto = kakaoService.getUserInfo(accessToken);

        userService.saveUser(userDto);

        // 카카오로 로그인 해서 JWT 받아오기
        TokenDto tokenDto = userService.login(userDto);

        return ResponseEntity.ok().body(tokenDto);
    }

    @PostMapping(value = "/google" )
    public ResponseEntity<TokenDto> returnco(@RequestBody UserRequestDto userRequestDto, HttpServletResponse response,
                                             HttpServletRequest request) throws IOException, GeneralSecurityException {
        // 구글로부터 사용자 받아오기
        UserDto userDto = googleService.getUserInfo(userRequestDto.getCredential());

        userService.saveUser(userDto);

        // 카카오로 로그인 해서 JWT 받아오기
        TokenDto tokenDto = userService.login(userDto);

        return ResponseEntity.ok().body(tokenDto);
    }

    //refreshToken 을 이용하여 accessToken 가져오기
    @GetMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new RuntimeException("JWT Token이 존재하지 않습니다.");
        }
        String refreshToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        TokenDto tokens = userService.refresh(refreshToken);  //refreshToken 을 넣으면 accessToken 이 반환 됨
        response.setHeader(AT_HEADER, tokens.getAccessToken());   //위의 accessToken 을 헤더에 넣고
        if (tokens.getRefreshToken() != null) {              // refreshToken 의 만료기간이 다가와서 새로 받은 refreshToken 이 있다면
            response.setHeader(RT_HEADER, tokens.getRefreshToken());  // refreshToken 도 같이 헤더에 넣는다.
        }
        return ResponseEntity.ok(tokens);   //화면에 출력
    }

    // 내정보 가져오기
    @GetMapping("/my")
    public ResponseEntity<Long> my(HttpServletRequest request) {
        User user = userService.getUserFromAccessToken();
        return ResponseEntity.ok(user.getId());
    }
}
