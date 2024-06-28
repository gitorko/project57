package com.demo.project57.exception;

public class CustomerException extends RuntimeException {
    private String message;

    public CustomerException() {
    }

    public CustomerException(String msg) {
        super(msg);
        this.message = msg;
    }
}
