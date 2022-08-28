package com.demo.project57.service;

import com.demo.project57.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerAsyncService customerAsyncService;

    public int getCustomerCount1() {
        return customerRepository.getCustomerCount();
    }

    @Transactional(timeout = 5)
    public int getCustomerCount2() {
        return customerRepository.getCustomerCount();
    }

    public int invokeAyncDbCall() {
        for (int i = 0; i < 5; i++) {
            //Query the DB 5 times
            customerAsyncService.getCustomerCount();
        }
        //Return value doesn't matter, we are invoking parallel requests to ensure connection pool if full.
        return 0;
    }

}
