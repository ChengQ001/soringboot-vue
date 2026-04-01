package com.chengq.app.exception;

import lombok.Getter;

/**
 * 可携带自定义业务码的业务异常（非受检），由 {@link GlobalExceptionHandler} 统一转为 {@link com.chengq.api.model.base.ApiResponse}。
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /** 默认 {@link BizCodes#BAD_REQUEST} */
    public BizException(String message) {
        this(BizCodes.BAD_REQUEST, message);
    }

    public BizException(String message, Throwable cause) {
        this(BizCodes.BAD_REQUEST, message, cause);
    }
}
