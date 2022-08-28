package com.demo.project57.repository;

import java.util.List;

import com.demo.project57.domain.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Query(value = "select count(*), pg_sleep(10) IS NULL from customer", nativeQuery = true)
    int getCustomerCount();
}
