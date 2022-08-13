package com.deadline826.bedi.login.Domain.Dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;


@RequiredArgsConstructor
@Repository
public class TempPasswordDao {


    private final int LIMIT_TIME = 1 * 60;

    private final StringRedisTemplate stringRedisTemplate;

    public void createPasswordCertification(String email, String rawPassword) {
        stringRedisTemplate.opsForValue()
                .set( email, rawPassword, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getPasswordCertification(String email) { // (4)
        return stringRedisTemplate.opsForValue().get( email);
    }

    public void removePasswordCertification(String email) { // (5)
        stringRedisTemplate.delete(email);
    }

}
