package com.demo.project57.service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import com.demo.project57.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAsyncService customerAsyncService;

    /**
     * Will block till the db returns data
     */
    public int getCustomerCount1(long delay) {
        return customerRepository.getCustomerCount(delay);
    }

    /**
     * Will time out after 5 seconds
     */
    @Transactional(timeout = 5)
    public int getCustomerCount2(long delay) {
        return customerRepository.getCustomerCount(delay);
    }

    /**
     * Will invoke db call from multiple threads
     */
    public void invokeAsyncDbCall(int threads, long delay) {
        for (int i = 0; i < threads; i++) {
            //Query the DB 'N' times
            customerAsyncService.getCustomerCount(delay);
        }
    }

    @SneakyThrows
    public String longRunningJob(Long delay) {
        log.info("Long running job started!");
        TimeUnit.SECONDS.sleep(delay);
        log.info("Long running job completed!");
        return "Job completed @" + LocalDateTime.now();
    }

}
