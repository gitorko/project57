package com.demo.project57.service;

import com.demo.project57.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public void invokeAsyncDbCall() {
        for (int i = 0; i < 10; i++) {
            //Query the DB 10 times
            customerAsyncService.getCustomerCount();
        }
        log.info("All Jobs Completed!");
    }

}
