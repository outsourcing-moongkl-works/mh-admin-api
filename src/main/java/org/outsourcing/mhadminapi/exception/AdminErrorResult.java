package org.outsourcing.mhadminapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminErrorResult {
    INVALID_ROLE(HttpStatus.FORBIDDEN, "부적절한 역할입니다."), // 403
    ALREADY_EXIST_ADMIN(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."), // 409
        ;
    private final HttpStatus httpStatus;
    private final String message;
}
