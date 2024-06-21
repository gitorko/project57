package com.demo.project57.config;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({RequestNotPermitted.class})
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void requestNotPermitted() {
    }

    @ExceptionHandler({BulkheadFullException.class})
    @ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
    public void bandwidthExceeded() {
    }
}
