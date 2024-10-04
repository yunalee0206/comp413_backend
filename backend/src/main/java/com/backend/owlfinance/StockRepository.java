package com.backend.owlfinance;

import org.springframework.data.jpa.repository.JpaRepository;

// changed Long to String since we'll index in with the ticker string
// TOOD: figure out if that's the right choice bc maybe we'll have multiple
// stocks with the same ticker symbol but different IDs
interface StockRepository extends JpaRepository<Stock, String> {

}