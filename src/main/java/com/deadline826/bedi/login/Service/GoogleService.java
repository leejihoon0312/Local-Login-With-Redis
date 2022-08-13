package com.deadline826.bedi.login.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.deadline826.bedi.login.Domain.Authority;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.deadline826.bedi.login.Domain.Dto.UserDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoogleService {

    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList("24418312077-pu3t75in2eeo4o519hvvcick5mgf5h96.apps.googleusercontent.com"))
            // Or, if multiple clients access the backend:
            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build();

    public UserDto getUserInfo(String credential) throws GeneralSecurityException, IOException {

        String idTokenString = credential;
        UserDto userDto = new UserDto();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        System.out.println(idToken);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // user identifier
            String userId = payload.getSubject();

            // Get profile information from payload
            String name = (String) payload.get("name");
            String email = payload.getEmail();

            // user id를 password로 설정
            userDto.setId(Long.parseLong(userId.substring(3)));
            userDto.setEmail(email);
            userDto.setUsername(name);
            userDto.setPassword(email);
//            userDto.setAuthority(Authority.GOOGLE);

        } else {
            log.debug("구글 ID 토큰이 유효하지 않습니다.");
        }

        return userDto;
    }
}
