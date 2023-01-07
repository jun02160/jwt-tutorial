package jjun.server.jwttutorial.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.servlet.http.HttpServletRequest;
import jjun.server.jwttutorial.config.BaseResponse;
import jjun.server.jwttutorial.dto.oauth.LoginResponseDto;
import jjun.server.jwttutorial.dto.oauth.SignupRequestDto;
import jjun.server.jwttutorial.dto.oauth.SignupResponseDto;
import jjun.server.jwttutorial.service.AuthService;
import jjun.server.jwttutorial.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class OauthController {

    private final OauthService oauthService;


    //==  Kakao 로그인, 회원가입 ==//

    /**
     * 인가 코드로 카카오 서버에 Access Token 을 요청하고, 해당 토큰으로 유저 정보를 받아와 DB 에 저장하는 API
     * -> GET 방식으로 param 에 들어오는 인가코드를 추출하여 처리 로직 수행
     */
    @GetMapping("/login/kakao")
    public BaseResponse<LoginResponseDto> kakaoLogin(HttpServletRequest request) {

        String code = request.getParameter("code");
        String kakaoAccessToken = oauthService.getKakaoAccessToken(code).getAccessToken();
        return new BaseResponse<>(oauthService.kakaoLogin(kakaoAccessToken));
    }

    @PostMapping("/signup/kakao")
    public BaseResponse<SignupResponseDto> kakaoSignup(@RequestBody SignupRequestDto requestDto) {
        return new BaseResponse<>(oauthService.kakaoSignup(requestDto));
    }

}
