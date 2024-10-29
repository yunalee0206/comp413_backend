import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

public class PortfolioTest {

    @Test
    public void testGetAndSetBalance() {
        Portfolio portfolio = new Portfolio();
        portfolio.setBalance(1000.00);
        assertEquals(1000.00, portfolio.getBalance());
    }

    @Test
    public void testGetAndSetStocks() {
        Portfolio portfolio = new Portfolio();
        Map<String, Integer> stocks = new HashMap<>();
        stocks.put("AAPL", 10);
        portfolio.setStocks(stocks);
        assertEquals(10, portfolio.getStocks().get("AAPL"));
    }
}
