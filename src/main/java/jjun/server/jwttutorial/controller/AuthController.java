package jjun.server.jwttutorial.controller;

import jakarta.validation.Valid;
import jjun.server.jwttutorial.dto.LoginDto;
import jjun.server.jwttutorial.dto.TokenDto;
import jjun.server.jwttutorial.dto.TokenRequestDto;
import jjun.server.jwttutorial.jwt.JwtFilter;
import jjun.server.jwttutorial.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /**
     *  로그인 API
     *  - LoginDto 의 username, password를 파라미터로 받고 이를 이용해 UsernamePasswordAuthenticationToken 생성
     *  - authenticationToken을 이용해 Authentication 객체 생성
     *  - authenticate() 메소드 실행 시, loadUserByUsername 메소드가 실행
     *  - 생성한 Authentication 객체는 SecurityContext 에 저장
     *  - Authentication 객체를 createToken() 메소드를 통해 JWT Token 발급
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        TokenDto jwt = authService.authenticate(loginDto);   // JWT Token 생성

        // Response Header 에 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);   // ResponseBody 에도 실어서 응답을 반환
    }

    /**
     * 토큰 재발급 API
     * - Access Token, Refresh Token String 을 담고 있는 TokenRequestDto 를 파라미터로 넘겨줌
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}
