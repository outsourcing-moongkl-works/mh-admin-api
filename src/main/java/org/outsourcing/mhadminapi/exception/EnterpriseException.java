package org.outsourcing.mhadminapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnterpriseException extends RuntimeException{
    private final EnterpriseErrorResult enterpriseErrorResult;
    @Override
    public String getMessage() {
        return enterpriseErrorResult.getMessage();
    }
}
