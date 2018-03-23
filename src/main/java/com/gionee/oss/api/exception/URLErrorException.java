package com.gionee.oss.api.exception;

/**
 * Created by yeqy on 2017/5/31.
 */
public class URLErrorException extends RuntimeException {

    public URLErrorException() {
    }

    public URLErrorException(String message) {
        super(message);
    }

    public URLErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
