package com.demo.project57.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.demo.project57.service.CustomerService;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final RestTemplate restTemplate;
    private final CustomerService customerService;

    List<String> names = new ArrayList<>();

    @GetMapping("/api/time")
    public String getServerTime() {
        log.info("Getting server time!");
        String podName = System.getenv("HOSTNAME");
        return "Pod: " + podName + " : " + LocalDateTime.now();
    }

    /**
     * Will block the tomcat threads and hence no other requests can be processed
     */
    @GetMapping("/api/echo1/{name}")
    public String echo1(@PathVariable String name) {
        log.info("echo1 received echo request: {}", name);
        longRunningJob(true);
        return "Hello " + name;
    }

    /**
     * Will time out after 1 second so other requests can be processed.
     */
    @GetMapping("/api/echo2/{name}")
    @TimeLimiter(name = "service1-tl")
    public CompletableFuture<String> echo2(@PathVariable String name) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("echo2 received echo request: {}", name);
            longRunningJob(false);
            return "Hello " + name;
        });
    }

    /**
     * API calling an external API that is not responding
     * Since we don't have an external API we are using the echo1 api
     *
     * Here timeout on the rest template is configured
     */
    @GetMapping("/api/echo3/{name}")
    public String echo3(@PathVariable String name) {
        log.info("echo3 received echo request: {}", name);
        String response = restTemplate.exchange("http://localhost:31000/api/echo2/john", HttpMethod.GET, null, String.class)
                .getBody();
        log.info("Got response: {}", response);
        return response;
    }

    /**
     * Over user of db connection by run-away method
     */
    @GetMapping("/api/many-db-call")
    public int manyDbCall() {
        log.info("manyDbCall invoked!");
        return customerService.invokeAyncDbCall();
    }

    /**
     * Slow query without timeout
     * Explicit delay of 10 seconds introduced in DB query
     */
    @GetMapping("/api/count1")
    public int getCount1() {
        log.info("dbCall invoked!");
        return customerService.getCustomerCount1();
    }

    /**
     * Slow query with timeout
     * Explicit delay of 10 seconds introduced in DB query
     */
    @GetMapping("/api/count2")
    public int getCount2() {
        log.info("dbCall invoked!");
        return customerService.getCustomerCount2();
    }

    /**
     * Create spike in memory
     * List keeps growing on each call and eventually causes OOM error
     */
    @GetMapping("/api/memory-leak")
    public ResponseEntity<?> memoryLeak() {
        log.info("Inserting customers to memory");
        for (int i = 0; i < 999999; i++) {
            names.add("customer_" + i);
        }
        return ResponseEntity.ok("DONE");
    }

    @SneakyThrows
    private void longRunningJob(Boolean fixedDelay) {
        if (fixedDelay) {
            TimeUnit.MINUTES.sleep(1);
        } else {
            //Randomly fixedDelay the job
            Random rd = new Random();
            if (rd.nextBoolean()) {
                TimeUnit.MINUTES.sleep(1);
            }
        }
    }
}
