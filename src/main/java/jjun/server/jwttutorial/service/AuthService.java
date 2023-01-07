package jjun.server.jwttutorial.service;

import jakarta.servlet.http.HttpServletRequest;
import jjun.server.jwttutorial.entity.Account;
import jjun.server.jwttutorial.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;

    /**
     * 사용자 정보를 가져오는 메소드
     * Request Header 에 Authorization 항목으로 토큰이 오면, 인증된 사용자에 대해 정보를 가져와 Account 타입으로 반환
     */
    public Account getAccountInfo(HttpServletRequest request) {
        String authenticAccount = (String) request.getAttribute("authenticAccount");
        Account account = accountRepository.findByEmail(authenticAccount).orElseThrow();
        System.out.println("AccountService 실행: " + account);
        return account;
    }
}
