package jjun.server.jwttutorial.config;

import lombok.Getter;

/**
 * 에러 코드 관리 : 임의로 코드와 메시지 지정
 */
@Getter
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요"),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false, 2003, "권한이 없는 유저의 접근입니다."),

    // user 관련
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요"),
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false, 2017, "중복된 이메일입니다."),

    POST_USERS_EMPTY_PASSWORD(false, 2020, "비밀번호를 입력해주세요."),
    USERS_PASSWORD_FORMAT(false, 2021, "비밀번호는 8자 이상의 영문 대소문자와 특수문자로 구성해야 합니다."),


    /**
     * 3000 : Response 오류
     */
    // common
    RESPONSE_ERROR(false, 3000, "값을 불러오는 데 실패했습니다."),

    // user 관련
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false, 3014, "존재하지 않는 아이디이거나 비밀번호가 틀렸습니다."),
    BANNED_USER_IN_LOGIN(false, 3015, "정지된 유저이므로 로그인이 불가합니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패했습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패했습니다."),

    // [PATCH] user 정보 수정 시
    MODIFY_FAIL_USERNAME(false, 4014, "회원 이름을 변경하는 데 실패했습니다."),
    MODIFY_FAIL_POSTS_INFO(false, 4015, "게시글 정보 수정에 실패했습니다."),

    DELETE_FAIL_POST(false, 4100, "게시글 삭제에 실패했습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패했습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패했습니다.");

    /**
     * 5000, 6000 : 필요 시 추가 구현
     */


    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
