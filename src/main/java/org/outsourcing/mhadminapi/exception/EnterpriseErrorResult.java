package org.outsourcing.mhadminapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EnterpriseErrorResult {
    ENTERPRISE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 기업을 찾을 수 없습니다."),
    INVALID_LOGOIMG_SIZE(HttpStatus.BAD_REQUEST, "로고 이미지 크기가 너무 큽니다."),
    LOGOIMG_UPLOAD_FAILED(HttpStatus.CONFLICT, "로고 이미지 업로드에 실패했습니다."),
    INVALID_STORYIMG_SIZE(HttpStatus.BAD_REQUEST, "스토리 이미지 크기가 너무 큽니다."),
    STORYIMG_UPLOAD_FAILED(HttpStatus.CONFLICT, "스토리 이미지 업로드에 실패했습니다."),
    STORYIMG_DELETE_FAILED(HttpStatus.CONFLICT, "스토리 이미지 삭제에 실패했습니다."),
    WRONG_PASSWORD(HttpStatus.FORBIDDEN, "잘못된 비밀번호입니다." ),
    STORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 스토리를 찾을 수 없습니다."), NOT_APPROVED_ENTERPRISE(HttpStatus.FORBIDDEN, "승인되지 않은 기업입니다."),
    BLOCKED_ENTERPRISE(HttpStatus.FORBIDDEN, "차단된 기업입니다."),;
    private final HttpStatus httpStatus;
    private final String message;
}
