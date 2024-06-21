package com.demo.project57;

import com.demo.project57.domain.Customer;
import com.demo.project57.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner seedData(CustomerRepository customerRepository) {
        return args -> {
            customerRepository.deleteAll();
            for (int i = 0; i < 10; i++) {
                customerRepository.save(Customer.builder()
                        .name("customer_" + i)
                        .phone("phone_" + i)
                        .city("city_" + i)
                        .build());
            }
        };
    }
}
