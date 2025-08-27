package com.example.textexpander.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.textexpander.entity.Signature;

import java.util.Optional;

public interface SignatureRepository extends JpaRepository<Signature, Long> {
    Optional<Signature> findByName(String name);
}