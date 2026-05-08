package com.gymmanage.common;

public class BizException extends RuntimeException {
    private final int code;
    private final int httpStatus;

    public BizException(int code, int httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public static BizException badRequest(String message) {
        return new BizException(40000, 400, message);
    }

    public static BizException unauthorized(String message) {
        return new BizException(40100, 401, message);
    }

    public static BizException notFound(String message) {
        return new BizException(40400, 404, message);
    }

    public int getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}

