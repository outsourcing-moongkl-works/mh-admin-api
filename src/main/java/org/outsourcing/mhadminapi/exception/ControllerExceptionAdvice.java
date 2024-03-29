package org.outsourcing.mhadminapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ResponseDto> handleAdminException(AdminException e) {

        log.error("AdminException : " + e.getMessage());

        final AdminErrorResult errorResult = e.getAdminErrorResult();

        final ResponseDto responseDto = ResponseDto.error(errorResult.getMessage());

        return ResponseEntity.status(errorResult.getHttpStatus()).body(responseDto);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResponseDto> authException(AuthException e){

        log.error("AuthException : " + e);

        final ResponseDto responseDto = ResponseDto.error(e.getMessage().toString());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleException(Exception e) {

        log.error("Exception " + e);

        if(e.getClass().getName().equals("org.springframework.security.access.AccessDeniedException")){

            final ResponseDto responseDto = ResponseDto.error("권한이 없습니다.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
        }

        final ResponseDto responseDto = ResponseDto.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }

}

