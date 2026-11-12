package com.example.givy.domain.commerce.repository;

import com.example.givy.domain.commerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.givy.domain.recommendation.enums.InvestmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(Long productId);

    List<Product> findAllByRecommendationType(InvestmentType investmentType);
}
