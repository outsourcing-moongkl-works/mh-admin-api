package org.outsourcing.mhadminapi.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException{

    public AuthException(){
        super(HttpStatus.UNAUTHORIZED.toString());
    }
}