package jjun.server.jwttutorial.dto.oauth;

import jjun.server.jwttutorial.entity.Account;
import lombok.Data;

@Data
public class SignupResponseDto {

    Account account;
    String result;
}
