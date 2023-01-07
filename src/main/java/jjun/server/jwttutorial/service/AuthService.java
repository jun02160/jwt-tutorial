package jjun.server.jwttutorial.service;

import jakarta.servlet.http.HttpServletRequest;
import jjun.server.jwttutorial.dto.LoginDto;
import jjun.server.jwttutorial.dto.TokenDto;
import jjun.server.jwttutorial.dto.TokenRequestDto;
import jjun.server.jwttutorial.dto.oauth.SignupRequestDto;
import jjun.server.jwttutorial.entity.Account;
import jjun.server.jwttutorial.entity.RefreshToken;
import jjun.server.jwttutorial.jwt.TokenProvider;
import jjun.server.jwttutorial.repository.AccountRepository;
import jjun.server.jwttutorial.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //== 일반 Form Login 토큰 발급 로직 ==//

    /**
     * 사용자 정보를 가져오는 메소드
     * Request Header 에 Authorization 항목으로 토큰이 오면, 인증된 사용자에 대해 정보를 가져와 Account 타입으로 반환
     */
    @Transactional
    public Account getAccountInfo(HttpServletRequest request) {
        String authenticAccount = (String) request.getAttribute("authenticAccount");
        Account account = accountRepository.findByEmail(authenticAccount).orElseThrow();
        System.out.println("AccountService 실행: " + account);
        return account;
    }

    /**
     * 로그인 시 Token 을 발급해서 리턴하는 메소드
     */
    @Transactional
    public TokenDto authenticate(LoginDto loginDto) {
        // 1. Login 을 시도한 ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 2. 실제로 검증이 이루어지는 부분 (유저의 비밀번호 일치 여부 체크)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);   // 이때 커스텀한 UserDetailsService 의 loadByUsername 메소드가 실행

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.createToken(authentication);

        // 4. Refresh Token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(Long.valueOf(authentication.getName()))
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    @Transactional
    public TokenDto authenticate(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow();
        log.info("AuthService-login: 계정을 찾았습니다 {}", account);

        // 토큰 발행
        TokenDto tokenDto = tokenProvider.createToken(email);

        // RefreshToken DB 에 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(account.getAccountId())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        log.info("토큰 발급과 저장을 완료했습니다.");

        return tokenDto;

    }

    /**
     * 토큰 만료 시 재발급하는 메소드
     */
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            log.debug("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 유저 정보 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 유저 ID 를 기반으로 Refresh Token 값을 가져오기
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            log.debug("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.createToken(authentication);

        // 6. Repository 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateToken(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }

    /**
     * 회원가입 요청에 대해 Access Token 과 Refresh Token 을 방급하고, Refresh Token 을 리포지토리에 저장하는 메소드
     */
    public TokenDto oauthSignup(SignupRequestDto requestDto) {
        Account account = requestDto.getAccount();
        return tokenProvider.createToken(account.getEmail());
    }


}
