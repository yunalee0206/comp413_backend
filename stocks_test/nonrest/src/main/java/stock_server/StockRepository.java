package stock_server;

import org.springframework.data.jpa.repository.JpaRepository;

interface StockRepository extends JpaRepository<Stock, Long> {

}