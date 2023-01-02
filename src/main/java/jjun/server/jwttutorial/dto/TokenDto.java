package jjun.server.jwttutorial.dto;

import lombok.*;

/**
 * Token 정보를 Response 할 때 사용할 TokenDto 객체
 */
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    private String token;

}
