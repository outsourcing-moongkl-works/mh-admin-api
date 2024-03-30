package org.outsourcing.mhadminapi.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter @RequiredArgsConstructor
public class EnterpriseBlockException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final String message;
    public EnterpriseBlockException(String message) {
        super(message);
        this.httpStatus = HttpStatus.FORBIDDEN;
        this.message = message;
    }
}
