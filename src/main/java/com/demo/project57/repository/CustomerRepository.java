package com.demo.project57.repository;

import com.demo.project57.domain.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    /**
     * Simulating a slow query by sleeping for 10 sec
     */
    @Query(value = "select count(*), pg_sleep(:delay) IS NULL from customer", nativeQuery = true)
    int getCustomerCount(@Param("delay") long delay);
}
