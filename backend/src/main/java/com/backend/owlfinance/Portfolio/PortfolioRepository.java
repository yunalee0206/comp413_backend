package com.backend.owlfinance;

import org.springframework.data.jpa.repository.JpaRepository;

interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}