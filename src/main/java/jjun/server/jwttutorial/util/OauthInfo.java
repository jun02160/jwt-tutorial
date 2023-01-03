package jjun.server.jwttutorial.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OauthInfo {
    String provider;
    String userId;
}
