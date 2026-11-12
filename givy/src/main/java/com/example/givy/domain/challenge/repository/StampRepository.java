package com.example.givy.domain.challenge.repository;

import com.example.givy.domain.challenge.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Long> {
    Optional<Stamp> findByName(String name);
}
