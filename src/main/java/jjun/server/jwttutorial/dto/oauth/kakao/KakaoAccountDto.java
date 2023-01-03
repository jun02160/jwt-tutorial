package jjun.server.jwttutorial.dto.oauth.kakao;

import lombok.Data;

import java.util.Properties;

@Data
public class KakaoAccountDto {

    /*

    [Kakao] 동의 항목
    필수 - 닉네임 (profile_nickname)
    선택 - 카카오계정 이메일 (account_email)

    *카카오 Developers REST API 문서 참고
    https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#kakaoaccount

     */

    public Long userId;   // 회원번호
    public String connectedAt;  // 서비스에 연결된 시각(UTC)
    public Properties properties;
    public KakaoAccount kakaoAccount;

    @Data
    public class KakaoAccount {  // 내부 클래스로 정의
        public Boolean profile_nickname_agreement;
        public Boolean email_needs_agreement;
        public Boolean is_email_valid;
        public Boolean is_email_verified;
        public Boolean has_email;

        public String email;
        public KakaoProfile profile;

        @Data
        public class KakaoProfile {
            public String nickname;
        }
    }
}
