package com.example.givy.domain.guide.repository;

import com.example.givy.domain.guide.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {

}
