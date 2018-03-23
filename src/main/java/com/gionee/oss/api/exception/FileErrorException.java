package com.gionee.oss.api.exception;

/**
 * Created by yeqy on 2017/6/1.
 */
public class FileErrorException extends RuntimeException {
    public FileErrorException() {
    }

    public FileErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileErrorException(String message) {
        super(message);
    }
}
