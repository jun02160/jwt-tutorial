package jjun.server.jwttutorial.dto.oauth;

import jjun.server.jwttutorial.entity.Account;
import lombok.Data;

/**
 * Kakao Login으로 회원가입 시 사용하는 DTO 객체
 */
@Data
public class SignupRequestDto {

    public String nickname;
    public String picture;
    public Account account;
}
