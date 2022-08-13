package com.deadline826.bedi.login.Controller;

import com.deadline826.bedi.SMS.Domain.Dto.PhoneNumberDto;
import com.deadline826.bedi.SMS.Domain.Dto.SmsCertificationRequest;
import com.deadline826.bedi.SMS.Service.SmsCertificationService;
import com.deadline826.bedi.Token.Domain.Dto.TokenDto;
import com.deadline826.bedi.exception.AuthenticationNumberMismatchException;
import com.deadline826.bedi.exception.DuplicateEmailException;
import com.deadline826.bedi.login.Domain.Dto.LoginDto;
import com.deadline826.bedi.login.Domain.Dto.UserDto;
import com.deadline826.bedi.login.Domain.User;
import com.deadline826.bedi.login.Service.UserService;
import com.deadline826.bedi.login.Service.VerifyPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Random;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LoginController {


    private final UserService userService;
    private final VerifyPasswordService verifyPasswordService;
    Random random = new Random();



    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) throws DuplicateEmailException {


        while (true){  //고유 아이디 생성
            userDto.setId(Long.parseLong(String.valueOf(1000000000 + random.nextInt(900000000))));
            User user = userService.findUserById(userDto.getId());
            System.out.println("user = " + user);
            if (user==null)
                break;
        }

        //유저정보 저장
        userService.saveUser(userDto);


        return ResponseEntity.ok().body("회원가입 완료");
    }


    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto)
            throws AuthenticationNumberMismatchException {

        // 이메일로 유저를 불러와
        User user = userService.findUserByEmail(loginDto.getEmail());


        // dto로 변환하고
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setPhone(user.getPhone());
        userDto.setPassword(loginDto.getPassword());
        userDto.setId(user.getId());

        // redis 를 아용하여 로그인 정보를 1분간 임시저장한다 (PW 검사시 이용됨)
        verifyPasswordService.makeTempPasswordStorage(loginDto.getEmail(),loginDto.getPassword());

        //변환된 dto로 로그인 진행
        TokenDto loginToken = userService.login(userDto);


        // 토큰정보를 반환한다
        return ResponseEntity.ok().body(loginToken);
    }
}
