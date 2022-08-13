package com.deadline826.bedi.SMS.Service;

import com.deadline826.bedi.SMS.Domain.Dao.SmsCertificationDao;
import com.deadline826.bedi.SMS.Domain.Dto.SmsCertificationRequest;
import com.deadline826.bedi.SMS.Template.SmsMessageTemplate;
import com.deadline826.bedi.exception.AuthenticationNumberMismatchException;
import com.deadline826.bedi.exception.SmsSendFailedException;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class SmsCertificationService {

    @Value("${spring.api_key}")
    String api_key;

    @Value("${spring.api_secret}")
    String api_secret;

    private final SmsCertificationDao smsCertificationDao;


    // 인증 메세지 내용 생성
    public String makeSmsContent(String certificationNumber) {
        SmsMessageTemplate content = new SmsMessageTemplate();
        return content.builderCertificationContent(certificationNumber);
    }

    public HashMap<String, String> makeParams(String phoneNumber, String text) {
        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNumber);
        params.put("from", "010-7572-7743");
        params.put("type", "SMS");

        params.put("text", text);
        return params;
    }

    // coolSms API를 이용하여 인증번호 발송하고, 발송 정보를 Redis에 저장
    public void sendSms(String phone) {


        Random random = new Random();

        Message coolSms = new Message(api_key, api_secret);
        String randomNumber = String.valueOf(100000 + random.nextInt(900000));
        String content = makeSmsContent(randomNumber);
        HashMap<String, String> params = makeParams(phone, content);

        try {
            JSONObject result = coolSms.send(params);
            if (result.get("success_count").toString().equals("0")) {
                throw new SmsSendFailedException("전송실패");
            }
        } catch (CoolsmsException | SmsSendFailedException exception) {
            exception.printStackTrace();
        }

        smsCertificationDao.createSmsCertification(phone, randomNumber);
    }

    //사용자가 입력한 인증번호가 Redis에 저장된 인증번호와 동일한지 확인
    public void verifySms(SmsCertificationRequest requestDto) throws AuthenticationNumberMismatchException {
        if (isVerify(requestDto)) {
            throw new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다.");
        }
        smsCertificationDao.removeSmsCertification(requestDto.getPhone());
    }

    private boolean isVerify(SmsCertificationRequest requestDto) {
        return !(smsCertificationDao.hasKey(requestDto.getPhone()) &&
                smsCertificationDao.getSmsCertification(requestDto.getPhone())
                        .equals(requestDto.getCertificationNumber()));
    }
}