package com.credential.cubrism.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * 400 Bad Request
     * 클라이언트가 잘못된 요청을 보냈을 때 발생하는 에러 코드
     * ex) 필수 요청 파라미터가 누락된 경우
     */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_FIELD(HttpStatus.BAD_REQUEST, "잘못된 필드입니다."),
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 JWT 토큰입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다."),
    INVALID_VERIFY_CODE(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),


    /**
     * 401 Unauthorized
     * 클라이언트가 인증되지 않은 상태에서 보호된 리소스에 접근하려고 할 때 발생하는 에러 코드
     * ex) 로그인하지 않은 사용자가 로그인이 필요한 페이지에 접근할 때
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),


    /**
     * 403 Forbidden
     * 클라이언트가 리소스에 접근할 권한이 없을 때 발생하는 에러 코드
     * ex) 로그인은 했지만, 해당 리소스에 접근할 권한이 없을 때
     */
    VERIFY_CODE_EXPIRED(HttpStatus.FORBIDDEN, "인증번호가 만료되었습니다."),
    DELETE_DENIED(HttpStatus.FORBIDDEN, "본인만 삭제할 수 있습니다."),
    UPDATE_DENIED(HttpStatus.FORBIDDEN, "본인만 수정할 수 있습니다."),
    STUDY_GROUP_NOT_MEMBER(HttpStatus.FORBIDDEN, "스터디 그룹의 멤버가 아닙니다."),
    STUDY_GROUP_NOT_ADMIN(HttpStatus.FORBIDDEN, "스터디 그룹의 관리자가 아닙니다."),
    PENDING_MEMBER_NOT_FOUND(HttpStatus.FORBIDDEN, "가입 대기 중인 스터디 그룹 멤버가 아닙니다."),


    /**
     * 404 Not Found
     * 클라이언트가 요청한 리소스를 찾을 수 없을 때 발생하는 에러 코드
     * ex) 존재하지 않는 데이터를 조회할 때
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이메일입니다."),
    S3_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3버킷에 파일이 존재하지 않습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "일정이 존재하지 않습니다."),
    QUALIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "자격증이 존재하지 않습니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시판이 존재하지 않습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리가 존재하지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "대댓글이 존재하지 않습니다."),
    STUDY_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "스터디 그룹이 존재하지 않습니다."),
    STUDY_GROUP_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "스터디 그룹 목표가 존재하지 않습니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "관심 자격증이 존재하지 않습니다."),


    /**
     * 409 Conflict
     * 클라이언트의 요청이 서버의 상태와 충돌이 발생했을 때 발생하는 에러 코드
     * ex) 중복된 데이터가 존재할 때
     */
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    STUDY_GROUP_FULL(HttpStatus.CONFLICT, "스터디 그룹이 가득 찼습니다."),
    STUDY_GROUP_ALREADY_JOINED(HttpStatus.CONFLICT, "이미 가입한 스터디 그룹입니다."),
    STUDY_GROUP_ADMIN_LEAVE(HttpStatus.CONFLICT, "스터디 그룹의 관리자는 탈퇴할 수 없습니다."),
    SOCIAL_LOGIN_USER(HttpStatus.CONFLICT, "소셜 로그인을 통해 가입한 유저입니다."),
    FAVORITE_ALREADY_ADDED(HttpStatus.CONFLICT, "이미 추가된 자격증입니다."),


    /**
     * 500 Internal Server Error
     * 서버에 오류가 발생했을 때 발생하는 에러 코드
     * ex) 서버에서 처리되지 않은 예외가 발생했을 때
     */
    EMAIL_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
    S3_PRE_SIGNED_URL_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "Pre-Signed URL 생성에 실패했습니다."),
    SIGNIN_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "로그인에 실패했습니다."),
    LOGOUT_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "로그아웃에 실패했습니다."),
    USER_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND,"유저 목표가 존재하지 않습니다" ),
    GOAL_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "세부 목표를 찾을 수 없습니다" ),
    FCM_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
