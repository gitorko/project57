package com.demo.project57.config;

import java.util.concurrent.TimeoutException;

import com.demo.project57.exception.CustomerException;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({RequestNotPermitted.class})
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void tooManyRequestsError() {
        log.error("tooManyRequestsError");
    }

    @ExceptionHandler({BulkheadFullException.class})
    @ResponseStatus(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
    public void bandwidthExceededError() {
        log.error("bandwidthExceededError");
    }

    @ExceptionHandler({CallNotPermittedException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public void serviceNotAvailableError() {
        log.error("serviceNotAvailableError");
    }

    @ExceptionHandler({TimeoutException.class})
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public void requestTimeoutException() {
        log.error("requestTimeoutException");
    }

    @ExceptionHandler({CustomerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void badRequestError() {
        log.error("badRequestError");
    }
}
