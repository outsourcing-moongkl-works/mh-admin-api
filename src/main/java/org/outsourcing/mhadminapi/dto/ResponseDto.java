package org.outsourcing.mhadminapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private T payload;
    private String error;


    // 'success' 빌더 메소드는 payload만 설정합니다.
    @Builder(builderMethodName = "success")
    public static <T> ResponseDto<T> success(T payload) {
        ResponseDto<T> dto = new ResponseDto<>();
        dto.setPayload(payload);
        return dto;
    }

    // 'error' 빌더 메소드는 error 메시지만 설정합니다.
    @Builder(builderMethodName = "error")
    public static <T> ResponseDto<T> error(String error) {
        ResponseDto<T> dto = new ResponseDto<>();
        dto.setError(error);
        return dto;
    }
}