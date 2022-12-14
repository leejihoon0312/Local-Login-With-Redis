package com.deadline826.bedi.login.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.deadline826.bedi.Goal.Domain.Goal;
import com.deadline826.bedi.Token.Domain.RefreshToken;
import com.deadline826.bedi.exception.DuplicateEmailException;
import com.deadline826.bedi.login.Domain.User;

import com.deadline826.bedi.Token.Domain.Dto.TokenDto;
import com.deadline826.bedi.login.Domain.Dto.UserDto;

import com.deadline826.bedi.exception.CustomAuthenticationException;
import com.deadline826.bedi.Goal.repository.GoalRepository;
import com.deadline826.bedi.Token.repository.RefreshTokenRepository;
import com.deadline826.bedi.login.Service.UserService;
import com.deadline826.bedi.login.repository.UserRepository;

import com.deadline826.bedi.security.CustomAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static com.deadline826.bedi.security.JwtConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.jsonwebtoken.Jwts;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private CustomAuthenticationFilter authenticationFilter;
    private final GoalRepository goalRepository;

    @Override
    public void setCustomAuthenticationFilter(CustomAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    // ???????????? ??????
    public Date getExpireTime(String refreshtoken) {

//        String[] token = refreshtoken.split(" ");

        return Jwts.parser().setSigningKey(JWT_SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(refreshtoken)
                .getBody()
                .getExpiration();
    }

    // ????????? ?????? ??? JWT?????? ??????
    //return ??? ??????????????????
    public TokenDto login(UserDto userDto) {
        try {

            //CustomAuthenticationFilter ??? attemptAuthentication ?????? ??????
            Authentication authentication = authenticationFilter.attemptAuthentication(userDto);

            //????????? ?????? ????????? ??????
            String email = authentication.getPrincipal().toString();

            //????????? ?????? ????????? ????????? ?????? ?????? ????????????
            Optional<User> user = userRepository.findByEmail(email);
            String random_id = user.get().getId().toString();


            //?????? ??????
            String accessToken = JWT.create()
                    .withSubject(random_id)  //  ?????? ???
                    .withExpiresAt(new Date(System.currentTimeMillis() + AT_EXP_TIME))  // ?????? ????????????
                    .withClaim("username", userDto.getUsername())   //????????? ??????
                    .withIssuedAt(new Date(System.currentTimeMillis()))  // ?????? ????????????
                    .sign(Algorithm.HMAC256(JWT_SECRET));  //JWT_SECRET ?????? ?????????
            String refreshToken = JWT.create()
                    .withSubject(random_id)
                    .withExpiresAt(new Date(System.currentTimeMillis() + RT_EXP_TIME))
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .sign(Algorithm.HMAC256(JWT_SECRET));

            // Refresh Token DB??? ??????

            RefreshToken remainRefreshToken = user.get().getRefreshToken();
            if (remainRefreshToken!=null){
                remainRefreshToken.setToken(refreshToken);
            }

            else{
                RefreshToken newRefreshToken = new RefreshToken();
                newRefreshToken.setToken(refreshToken);
                RefreshToken save = refreshTokenRepository.save(newRefreshToken);

                //RefreshToken ??? ???????????? user ??? ???????????? ??????
                updateRefreshToken(random_id, save);
            }



            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .refreshTokenExpireTime(getExpireTime(refreshToken))  // ??? ??????
                    .build();

        } catch (AuthenticationException e) {
            throw new CustomAuthenticationException("????????? ?????????");
        }
        catch (NullPointerException e) {
            throw new CustomAuthenticationException("????????? ?????????");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //?????? ?????? ????????????
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : ???????????? ?????? ??? ????????????."));

        // authorities ?????? Collections.EMPTY_LIST??? ?????? Role ?????? ?????????????????? ??????
        //CustomAuthProvider ??? authenticate ??? ??????
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),Collections.EMPTY_LIST);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {

        //?????? ?????? ????????????
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : ???????????? ?????? ??? ????????????."));

        // authorities ?????? Collections.EMPTY_LIST??? ?????? Role ?????? ?????????????????? ??????
        //CustomAuthProvider ??? authenticate ??? ??????
        return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassword(),Collections.EMPTY_LIST);
    }

    @Override
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {

        //?????? ?????? ????????????
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("UserDetailsService - loadUserByUsername : ???????????? ?????? ??? ????????????."));

        // authorities ?????? Collections.EMPTY_LIST??? ?????? Role ?????? ?????????????????? ??????
        //CustomAuthProvider ??? authenticate ??? ??????
        return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassword(),Collections.EMPTY_LIST);
    }

    @Override
    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            return null;
        else
            return user.get();

    }

    @Override
    public User findUserByEmail(String email) {
        try {
            Optional<User> user = userRepository.findByEmail(email);
            return user.get();
        }
        catch (NoSuchElementException e){
            throw new NoSuchElementException();
        }

    }


    @Override
    public void saveUser(UserDto dto) throws DuplicateEmailException {
        boolean isSave = validateDuplicateUserEmail(dto);  //????????????
        if (!isSave) {
            dto.encodePassword(passwordEncoder.encode(dto.getPassword()));  //???????????? ????????? ??????
            User user = dto.toEntity();
            userRepository.save(user);   //????????????

        }
    }

    private boolean validateDuplicateUserEmail(UserDto dto) throws DuplicateEmailException {
        if (userRepository.existsByEmail(dto.getEmail())) {     // ???????????? ??????????????? ??????
            throw new DuplicateEmailException("?????? ???????????? ????????? ?????????");
        }
        return false;
    }

    // =============== TOKEN ============ //

    @Override
    public void updateRefreshToken(String id, RefreshToken refreshToken) {
        User user = userRepository.findById((Long.parseLong( id))).orElseThrow(() -> new RuntimeException("???????????? ?????? ??? ????????????."));
        user.updateRefreshToken(refreshToken);
    }


    @Override
    public User getUserFromAccessToken() {
        try {
            // Authorization filter?????? SecurityContextHolder?????? set??? authentication ????????? ????????????.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getPrincipal().toString();
            return userRepository.findById(Long.parseLong(userId)).get();
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    //return ??? ??????????????????
    @Override
    public TokenDto refresh(String refreshToken) {

        // === Refresh Token ????????? ?????? === //
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET)).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);

        // === Access Token ????????? === //
        long now = System.currentTimeMillis();
        String id = decodedJWT.getSubject();

        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("???????????? ?????? ??? ????????????."));

        if (!user.getRefreshToken().getToken().equals(refreshToken)) {
            throw new JWTVerificationException("???????????? ?????? Refresh Token ?????????.");
        }
        String accessToken = JWT.create()
                .withSubject(user.getId().toString())
                .withExpiresAt(new Date(now + AT_EXP_TIME))
                .sign(Algorithm.HMAC256(JWT_SECRET));

        // === ??????????????? Refresh Token ??????????????? ?????? ?????? ???????????? ?????? === //
        // === Refresh Token ???????????? ????????? 1?????? ????????? ??? refresh token??? ?????? === //
        long refreshExpireTime = decodedJWT.getClaim("exp").asLong() * 1000;
        long diffDays = (refreshExpireTime - now) / 1000 / (24 * 3600);
        long diffMin = (refreshExpireTime - now) / 1000 / 60;
        if (diffMin < 5) {
            String newRefreshToken = JWT.create()
                    .withSubject(user.getId().toString())  // ???????????? ???????????? ?????? ???
                    .withExpiresAt(new Date(now + RT_EXP_TIME))
                    .sign(Algorithm.HMAC256(JWT_SECRET));
            user.getRefreshToken().setToken(newRefreshToken);

            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .refreshTokenExpireTime(getExpireTime(newRefreshToken))    // ??? ??????
                    .build();
        }

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken("??????????????? ???????????????")   // ??? ??????
                .build();



    }
}
