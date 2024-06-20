package com.demo.project57.repository;

import com.demo.project57.domain.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    /**
     * Simulating a slow query by sleeping for 10 sec
     */
    @Query(value = "select count(*), pg_sleep(10) IS NULL from customer", nativeQuery = true)
    int getCustomerCount();
}
