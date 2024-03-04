package org.outsourcing.mhadminapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminException extends RuntimeException{
    private final AdminErrorResult adminErrorResult;
    @Override
    public String getMessage() {
        return adminErrorResult.getMessage();
    }
}
