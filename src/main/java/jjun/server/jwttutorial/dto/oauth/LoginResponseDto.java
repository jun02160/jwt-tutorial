package jjun.server.jwttutorial.dto.oauth;

import jjun.server.jwttutorial.entity.Account;
import lombok.Data;

@Data
public class LoginResponseDto {

    public boolean loginSuccess;
    public Account account;
    public String kakaoAccessToken;
}
