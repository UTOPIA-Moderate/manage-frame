package com.manage.common.exception;

import lombok.Getter;
import com.manage.common.result.ResultCode;

@Getter
public class BaseException extends RuntimeException {

    private final int code;
    private final String message;

    public BaseException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_ERROR.getCode();
        this.message = message;
    }
}
