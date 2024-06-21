package com.demo.project57.controller;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.demo.project57.service.CustomerService;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    List<String> names = new ArrayList<>();

    @SneakyThrows
    @GetMapping("/time")
    public String getTime() {
        log.info("Getting server time!");
        String podName = InetAddress.getLocalHost().getHostName();
        return "Pod: " + podName + " : " + LocalDateTime.now();
    }

    /**
     * Will block the tomcat threads and hence no other requests can be processed
     */
    @GetMapping("/job1/{delay}")
    public String job1(@PathVariable Long delay) {
        log.info("job1 request received, delay: {}", delay);
        return customerService.longRunningJob(delay);
    }

    /**
     * Will not block the tomcat threads and hence no other requests can be processed
     */
    @GetMapping("/job2/{delay}")
    public CompletableFuture<String> job2(@PathVariable Long delay) {
        log.info("job2 request received, delay: {}", delay);
        return CompletableFuture.supplyAsync(() -> {
            return customerService.longRunningJob(delay);
        });
    }

    /**
     * The @TimeLimiter will timeout if the job takes too long.
     * The job will still run in the background, There is no way to kill a thread in java you can only interrupt.
     */
    @GetMapping("/job3/{delay}")
    @TimeLimiter(name = "project57-tl")
    public CompletableFuture<String> job3(@PathVariable Long delay) {
        log.info("job3 request received, delay: {}", delay);
        return CompletableFuture.supplyAsync(() -> {
            return customerService.longRunningJob(delay);
        });
    }

    /**
     * API calling an external API that is not responding
     * Here timeout on the rest client is configured
     */
    @GetMapping("/job4/{delay}")
    public String job4(@PathVariable Long delay) {
        log.info("job4 request received, delay: {}", delay);
        String result = restClient.get()
                .uri("/users/1?_delay=" + (delay * 1000))
                .retrieve()
                .body(String.class);
        log.info("job4 response: {}", result);
        return result;
    }

    /**
     * Over user of db connection by run-away thread pool
     */
    @GetMapping("/job5/{threads}")
    public void job5(@PathVariable int threads) {
        log.info("job5 request received, threads: {}", threads);
        customerService.invokeAsyncDbCall(threads, 1);
    }

    /**
     * Slow query without timeout
     * Explicit delay of 10 seconds introduced in DB query
     */
    @GetMapping("/job6/{delay}")
    public int job6(@PathVariable Long delay) {
        log.info("job6 request received, delay: {}", delay);
        return customerService.getCustomerCount1(delay);
    }

    /**
     * Slow query with timeout of 5 seconds
     */
    @GetMapping("/job7/{delay}")
    public int job7(@PathVariable Long delay) {
        log.info("job7 request received, delay: {}", delay);
        return customerService.getCustomerCount2(delay);
    }

    /**
     * Create spike in memory
     * List keeps growing on each call and eventually causes OOM error
     */
    @GetMapping("/job8/{records}")
    public ResponseEntity job8(@PathVariable Long records) {
        log.info("job8 request received");
        for (int i = 0; i < records; i++) {
            names.add("customer_" + i);
        }
        return ResponseEntity.ok().build();
    }

}
