package com.deadline826.bedi.SMS.Controller;

import com.deadline826.bedi.SMS.Domain.Dto.PhoneNumberDto;
import com.deadline826.bedi.SMS.Domain.Dto.SmsCertificationRequest;
import com.deadline826.bedi.SMS.Service.SmsCertificationService;
import com.deadline826.bedi.exception.AuthenticationNumberMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class SmsController {


    private final SmsCertificationService smsCertificationService;


    //인증번호 발송
    @PostMapping("/send")
    public ResponseEntity<String> sendSms(@RequestBody PhoneNumberDto phoneNumberDto) {
        smsCertificationService.sendSms(phoneNumberDto.getPhone());
        return ResponseEntity.ok().body("인증번호 발송");
    }

    //인증번호 확인
    @PostMapping("/confirm")
    public ResponseEntity<String> SmsVerification(@RequestBody SmsCertificationRequest requestDto) throws AuthenticationNumberMismatchException {
        smsCertificationService.verifySms(requestDto);
        return ResponseEntity.ok().body("인증 완료");
    }
}
