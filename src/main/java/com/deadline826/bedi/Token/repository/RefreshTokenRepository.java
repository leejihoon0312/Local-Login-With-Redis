package com.deadline826.bedi.Token.repository;

import com.deadline826.bedi.Token.Domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}