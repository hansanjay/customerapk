package com.org.tsd.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends Exception {
	
    private static final long serialVersionUID = 1L;
    
	private final int code;
    private final HttpStatus httpStatusCode;

    public ApplicationException(int code, String message) {
        this(code, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ApplicationException(int code, String message, HttpStatus httpStatusCode) {
        super(message);
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    public ApplicationException(int code, String message, Exception cause) {
        this(code, message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ApplicationException(int code, String message, Exception cause, HttpStatus httpStatusCode) {
        super(message, cause);
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }
}
