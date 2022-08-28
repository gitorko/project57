package com.demo.project57.service;

import com.demo.project57.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class CustomerAsyncService {
    private final CustomerRepository customerRepository;

    /**
     * Each method run in parallel causing connection pool to become full.
     * Explicitly creating many connections so we run out of connections
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void getCustomerCount() {
        log.info("getCustomerLike invoked!");
        int count = customerRepository.getCustomerCount();
        log.info("getCustomerLike count: {}", count);
    }

}
