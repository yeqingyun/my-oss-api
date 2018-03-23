package com.gionee.oss.api.exception;

/**
 * Created by yeqy on 2017/6/12.
 */
public class TargetPathException extends RuntimeException {

    public TargetPathException() {
    }

    public TargetPathException(String message) {
        super(message);
    }

    public TargetPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
