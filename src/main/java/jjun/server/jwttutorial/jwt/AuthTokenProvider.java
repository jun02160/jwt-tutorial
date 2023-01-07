package jjun.server.jwttutorial.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
public class AuthTokenProvider {

    private String expiry;

}
