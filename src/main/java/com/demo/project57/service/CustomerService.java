package com.demo.project57.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.demo.project57.domain.Customer;
import com.demo.project57.exception.CustomerException;
import com.demo.project57.repository.CustomerRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAsyncService customerAsyncService;
    AtomicLong counter = new AtomicLong();

    public Iterable<Customer> findAllCustomer() {
        return customerRepository.findAll();
    }

    public Iterable<Customer> findAllCustomerByPage(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

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

    @Retry(name = "project57-y1")
    public String getTime() {
        log.info("Getting time from api!");
        //Simulating a failure first 2 times
        if (counter.incrementAndGet() < 3) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(500));
        } else {
            counter = new AtomicLong();
            return String.valueOf(LocalDateTime.now());
        }
    }

    public Customer saveCustomer(Customer customer) {
        if (customer.getCity().equals("unknown")) {
            throw new CustomerException("Unknown city for customer!");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getByCity(String city) {
        return customerRepository.getByCity(city);
    }
}
