package com.demo.project57.controller;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.demo.project57.config.CloudConfig;
import com.demo.project57.domain.Customer;
import com.demo.project57.service.CustomerService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class HomeController {

    private final CustomerService customerService;
    private final RestClient restClient;
    private final CloudConfig cloudConfig;

    Map<MyKey, byte[]> customerMap = new HashMap<>();
    List<Customer> customerList;

    @SneakyThrows
    @GetMapping("/time")
    public String getTime() {
        log.info("Getting server time!");
        String podName = InetAddress.getLocalHost().getHostName();
        return "Pod: " + podName + " : " + LocalDateTime.now();
    }

    @GetMapping("/customer")
    public Iterable<Customer> findAllCustomer() {
        log.info("Finding All Customers!");
        return customerService.findAllCustomer();
    }

    @GetMapping("/customer-page")
    public Iterable<Customer> findAllCustomerByPage(Pageable pageable) {
        log.info("Finding All Customers By Page!");
        return customerService.findAllCustomerByPage(pageable);
    }

    /**
     * Will block the tomcat threads and hence no other requests can be processed
     */
    @GetMapping("/blocking-job/{delay}")
    public String blockingJob(@PathVariable Long delay) {
        log.info("blockingJob request received, delay: {}", delay);
        return customerService.longRunningJob(delay);
    }

    /**
     * Will not block the tomcat threads and hence no other requests can be processed
     */
    @GetMapping("/async-job/{delay}")
    public CompletableFuture<String> asyncJob(@PathVariable Long delay) {
        log.info("asyncJob request received, delay: {}", delay);
        return CompletableFuture.supplyAsync(() -> {
            return customerService.longRunningJob(delay);
        });
    }

    /**
     * The @TimeLimiter will timeout if the job takes too long.
     * The job will still run in the background, There is no way to kill a thread in java you can only interrupt.
     */
    @GetMapping("/timeout-job/{delay}")
    @TimeLimiter(name = "project57-t1")
    public CompletableFuture<String> timeoutJob(@PathVariable Long delay) {
        log.info("timeoutJob request received, delay: {}", delay);
        return CompletableFuture.supplyAsync(() -> {
            return customerService.longRunningJob(delay);
        });
    }

    /**
     * API calling an external API that is not responding
     * Here timeout on the rest client is configured
     */
    @GetMapping("/external-api-job/{delay}")
    public String externalApiJob(@PathVariable Long delay) {
        log.info("externalApiJob request received, delay: {}", delay);
        String result = restClient.get()
                .uri("/users/1?_delay=" + (delay * 1000))
                .retrieve()
                .body(String.class);
        log.info("externalApiJob response: {}", result);
        return result;
    }

    /**
     * Over user of db connection by run-away thread pool
     */
    @GetMapping("/async-db-job/{threads}")
    public void asyncDbJob(@PathVariable int threads) {
        log.info("asyncDbJob request received, threads: {}", threads);
        customerService.invokeAsyncDbCall(threads, 1);
    }

    /**
     * Long-running query without timeout
     * Explicit delay of 10 seconds introduced in DB query
     */
    @GetMapping("/db-long-query-job/{delay}")
    public int dbLongQueryJob(@PathVariable Long delay) {
        log.info("dbLongQueryJob request received, delay: {}", delay);
        return customerService.getCustomerCount1(delay);
    }

    /**
     * Long-running query with timeout of 5 seconds
     */
    @GetMapping("/db-long-query-timeout-job/{delay}")
    public int dbLongQueryTimeoutJob(@PathVariable Long delay) {
        log.info("dbLongQueryTimeoutJob request received, delay: {}", delay);
        return customerService.getCustomerCount2(delay);
    }

    /**
     * Create memory leak and spike in heap memory
     * Map keeps growing on each call and eventually causes OOM error
     * If the key is unique the map should have fixed set of entries no matter how many times we invoke
     * Key in hashmap has to be immutable
     */
    @GetMapping("/memory-leak-job/{records}")
    public ResponseEntity memoryLeakJob(@PathVariable Long records) {
        log.info("memoryLeakJob request received");
        for (int i = 0; i < records; i++) {
            //By creating a non-immutable key it creates a memory leak
            customerMap.put(new MyKey("customer_" + i), new byte[100000]);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Will allow GC to recover the space
     */
    @GetMapping("/load-heap-job/{records}")
    public ResponseEntity loadHeapJob(@PathVariable Long records) {
        log.info("loadHeapJob request received");
        customerList = new ArrayList<>();
        for (int i = 0; i < records; i++) {
            //By creating a non-immutable key it creates a memory leak
            customerList.add(Customer.builder()
                    .id(Long.valueOf(i))
                    .name("customer_" + i)
                    .city("city_" + i)
                    .build());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Bulk head
     */
    @GetMapping("/bulk-head-job")
    @Bulkhead(name = "project57-b1")
    public String bulkHeadJob() {
        log.info("bulkHeadJob request received");
        return customerService.longRunningJob(5l);
    }

    /**
     * Rate limit
     */
    @GetMapping("/rate-limit-job")
    @RateLimiter(name = "project57-r1")
    public String rateLimitJob(@PathVariable Long delay) {
        log.info("rateLimitJob request received");
        return customerService.longRunningJob(5l);
    }

    @GetMapping("/retry-job")
    public String retryJob() {
        log.info("retryJob request received");
        return customerService.getTime();
    }

    /**
     * Secret Password generated using library Passay
     */
    @GetMapping("/password-gen-job/{delay}")
    public String passwordGenJob(@PathVariable Long delay) {
        log.info("passwordGenJob request received");
        List<CharacterRule> charList = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 2),
                new CharacterRule(EnglishCharacterData.LowerCase, 2),
                new CharacterRule(EnglishCharacterData.Digit, 2));
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String newPassword = passwordGenerator.generatePassword(15, charList);
        log.info("Password generated, Wont be printed!");
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = encoder.encode(newPassword);
        log.info("Encoded Password {}", encodedPassword);
        customerService.longRunningJob(delay);
        return encodedPassword;
    }

    @GetMapping("/feature-job")
    public String featureJob() {
        log.info("featureJob request received");
        if (cloudConfig.getNewFeatureFlag()) {
            return "Feature v2";
        } else {
            return "Feature v1";
        }
    }

    @AllArgsConstructor
    @Data
    class MyKey {
        String key;
    }

}
