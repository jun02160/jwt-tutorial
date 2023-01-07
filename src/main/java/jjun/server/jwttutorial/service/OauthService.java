package jjun.server.jwttutorial.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jjun.server.jwttutorial.config.BaseException;
import jjun.server.jwttutorial.config.BaseResponseStatus;
import jjun.server.jwttutorial.dto.TokenDto;
import jjun.server.jwttutorial.dto.oauth.LoginResponseDto;
import jjun.server.jwttutorial.dto.oauth.SignupRequestDto;
import jjun.server.jwttutorial.dto.oauth.SignupResponseDto;
import jjun.server.jwttutorial.dto.oauth.kakao.KakaoAccountDto;
import jjun.server.jwttutorial.dto.oauth.kakao.KakaoTokenDto;
import jjun.server.jwttutorial.entity.Account;
import jjun.server.jwttutorial.entity.Authority;
import jjun.server.jwttutorial.entity.RefreshToken;
import jjun.server.jwttutorial.repository.AccountRepository;
import jjun.server.jwttutorial.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository tokenRepository;
    private final AuthService authService;

    // 환경변수 가져오기
    @org.springframework.beans.factory.annotation.Value("${kakao.key}")
    String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect_uri}")
    String KAKAO_REDIRECT_URI;


    // 인가코드로 Kakao Access Token 을 요청하는 메소드
    public KakaoTokenDto getKakaoAccessToken(String code) {

        RestTemplate rt = new RestTemplate();  // 통신용 템플릿 for HTTP 통신 단순화 (스프링에서 지원하는 REST 서비스 호출방식)
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        /* Kakao 공식 문서에 따라 헤더,바디 값 구성 */
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);  // 인가 코드 요청 시 받은 인가 코드 값 from 프론트엔드

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        log.info("KakaoTokenRequest: {}", kakaoTokenRequest);

        // Kakao 로부터 Access Token 수신
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON Parsing (KakaoTokenDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            log.debug("Kakao Access Token(KakaoTokenDto) JSON Parsing 에 실패했습니다.");
        }

        return kakaoTokenDto;
    }

    // Kakao Access Token 으로 카카오 서버에 정보 요청하는 메소드
    public Account getKakaoInfo(String kakaoAccessToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        // POST 방식으로 API 서버에 요청을 보내고 Response 를 받아온다.
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        log.info("카카오 서버에서 정상적으로 데이터를 수신했습니다.");

        // Json Parsing (KakaoAccountDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoAccountDto kakaoAccountDto = null;
        try {
            kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
        } catch (JsonProcessingException e) {
            log.debug("KakaoInfo(KakaoAccountDto) JSON Parsing 에 실패했습니다.");
        }

        // KakaoAccountDto 에서 필요한 정보를 꺼내서 Account 객체로 매핑
        String email = kakaoAccountDto.getKakaoAccount().getEmail();
        String kakaoName = kakaoAccountDto.getKakaoAccount().getProfile().getNickname();

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        return Account.builder()
                .loginType("KAKAO")
                .email(email)
                .kakaoName(kakaoName)
                .authorities(Collections.singleton(authority))
                .build();
    }

    // 로그인 로직 : 이전에 회원가입을 한 적이 있는지를 판단하여 분기 처리
    // TODO 헤더와 바디를 구성하는 ResponseEntity 는 바깥으로 빼기
    public ResponseEntity<LoginResponseDto> kakaoLogin(String kakaoAccessToken) {
        // Kakao Access Token 으로 회원정보 받아오기
        Account account = getKakaoInfo(kakaoAccessToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setKakaoAccessToken(kakaoAccessToken);
        loginResponseDto.setAccount(account);

        try {
            TokenDto tokenDto = authService.authenticate(account.getEmail());
            loginResponseDto.setLoginSuccess(true);

            HttpHeaders headers = setTokenHeaders(tokenDto);
            return ResponseEntity.ok().headers(headers).body(loginResponseDto);
        } catch (Exception e) {
            loginResponseDto.setLoginSuccess(false);
            return ResponseEntity.ok(loginResponseDto);
        }
    }

    private HttpHeaders setTokenHeaders(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", tokenDto.getRefreshToken())
                .path("/")
                .maxAge(60*60*24*7)   // Cookie 유효기간 7일로 지정
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        headers.add("Set-cookie", cookie.toString());
        headers.add("Authorization", tokenDto.getAccessToken());

        return headers;
    }

    // 회원가입 요청 처리 메소드
    public ResponseEntity<SignupResponseDto> kakaoSignup(@RequestBody SignupRequestDto requestDto) throws BaseException {
        // 카카오 서버로부터 받아온 회원정보 DB 에 저장
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        Account newAccount = Account.builder()
                .loginType("KAKAO")
                .email(requestDto.getAccount().getEmail())
                .kakaoName(requestDto.getAccount().getKakaoName())
                .nickname(requestDto.getNickname())
                .picture(requestDto.getPicture())
                .authorities(Collections.singleton(authority))
                .build();
        accountRepository.save(newAccount);

        // 회원가입 상황에 따라 토큰 발급 후 헤더와 쿠키에 배치
        TokenDto tokenDto = authService.authenticate(newAccount.getEmail());
        saveRefreshToken(newAccount, tokenDto);

        HttpHeaders headers = setTokenHeaders(tokenDto);

        // 응답 작성
        SignupResponseDto responseDto = new SignupResponseDto();
        responseDto.setAccount(accountRepository.findByEmail(requestDto.getAccount().getEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DATABASE_ERROR)));
        responseDto.setResult("회원가입이 완료되었습니다.");
        return ResponseEntity.ok().headers(headers).body(responseDto);
    }

    private void saveRefreshToken(Account account, TokenDto tokenDto) {
        RefreshToken refreshToken = RefreshToken.builder()
                .key(account.getAccountId())
                .value(tokenDto.getRefreshToken())
                .build();

        tokenRepository.save(refreshToken);
        log.info("토큰 저장이 완료되었습니다 : 계정 아이디 - {}, refresh token - {}", account.getAccountId(), tokenDto.getRefreshToken());
    }


}
