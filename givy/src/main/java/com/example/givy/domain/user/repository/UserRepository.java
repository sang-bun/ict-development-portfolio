package com.example.givy.domain.user.repository;

import com.example.givy.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    Optional<Users> findByName(String name);
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(Long id);

}
