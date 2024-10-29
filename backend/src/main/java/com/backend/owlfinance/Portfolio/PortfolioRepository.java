package com.backend.owlfinance.Portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}