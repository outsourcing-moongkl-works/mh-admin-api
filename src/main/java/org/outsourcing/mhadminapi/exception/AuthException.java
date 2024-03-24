package org.outsourcing.mhadminapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException{

    public AuthException(){
        super(HttpStatus.UNAUTHORIZED.toString());
    }

}