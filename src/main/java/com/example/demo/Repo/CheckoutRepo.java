package com.example.demo.Repo;

import com.example.demo.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckoutRepo extends JpaRepository<Checkout,Long> {
    Optional<Checkout> findByCartId(String id);

    Optional<Checkout> findByCartIdAndOrderPlacedIsFalse(String id);

    List<Checkout> findAllByOrderPlacedIsTrue();
}
