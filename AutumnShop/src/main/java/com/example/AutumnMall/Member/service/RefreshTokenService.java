package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.RefreshToken;
import com.example.AutumnMall.Member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("새로운 refreshToken 저장되었습니다. token: {}", savedToken.getValue());
        return savedToken;
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByValue(refreshToken);
        if (existingToken.isPresent()) {
            refreshTokenRepository.delete(existingToken.get());
            log.info("refreshToken 삭제되었습니다. token: {}", refreshToken);
        } else {
            log.warn("삭제하려는 refreshToken 찾을 수 없습니다. token: {}", refreshToken);
        }
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByValue(refreshToken);
    }
}
