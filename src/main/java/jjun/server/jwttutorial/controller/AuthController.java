package jjun.server.jwttutorial.controller;

import jakarta.validation.Valid;
import jjun.server.jwttutorial.dto.LoginDto;
import jjun.server.jwttutorial.dto.TokenDto;
import jjun.server.jwttutorial.dto.token.TokenResponseDto;
import jjun.server.jwttutorial.entity.User;
import jjun.server.jwttutorial.jwt.JwtFilter;
import jjun.server.jwttutorial.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     *  로그인 API
     *  - LoginDto 의 username, password를 파라미터로 받고 이를 이용해 UsernamePasswordAuthenticationToken 생성
     *  - authenticationToken을 이용해 Authentication 객체 생성
     *  - authenticate() 메소드 실행 시, loadUserByUsername 메소드가 실행
     *  - 생성한 Authentication 객체는 SecurityContext 에 저장
     *  - Authentication 객체를 createToken() 메소드를 통해 JWT Token 발급
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponseDto> authorize(@Valid @RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);   // 이때 커스텀한 UserDetailsService 의 loadByUsername 메소드가 실행
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenResponseDto jwt = tokenProvider.createToken(authentication);   // JWT Token 생성

        // Response Header 에 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);   // ResponseBody 에도 실어서 응답을 반환
    }
}
