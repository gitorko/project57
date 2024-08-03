package com.demo.project57.repository;

import java.util.List;

import com.demo.project57.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /**
     * Simulating a slow query by sleeping for 10 sec
     */
    @Query(value = "select count(*), pg_sleep(:delay) IS NULL from customer", nativeQuery = true)
    int getCustomerCount(@Param("delay") long delay);

    @Query(value = "select * from customer where city = :city", nativeQuery = true)
    List<Customer> getByCity(@Param("city") String city);
}
