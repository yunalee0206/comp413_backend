package com.backend.owlfinance.repository;

import com.backend.owlfinance.entity.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DummyRepository extends JpaRepository<DummyEntity, String> {
}
