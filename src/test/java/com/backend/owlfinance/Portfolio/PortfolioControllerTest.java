import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.owlfinance.StockApplication;

@SpringBootTest(classes = StockApplication.class)
@AutoConfigureMockMvc
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllPortfolios() throws Exception {
        mockMvc.perform(get("/portfolios"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetPortfolioByUserId() throws Exception {
        mockMvc.perform(get("/portfolios/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.userId").value(1));
    }

    // TODO: Add back in once working.
    // @Test
    // public void testDeposit() throws Exception {
    //     mockMvc.perform(post("/portfolios/1/deposit")
    //            .contentType(MediaType.APPLICATION_JSON)
    //            .content("{\"amount\": 500.00}"))
    //            .andExpect(status().isOk())
    //            .andExpect(jsonPath("$.balance").value(500.00));
    // }
    // TODO: Add back in once working.
    // @Test
    // public void testWithdrawWithInsufficientFunds() throws Exception {
    //     mockMvc.perform(post("/portfolios/1/withdraw")
    //            .contentType(MediaType.APPLICATION_JSON)
    //            .content("{\"amount\": 10000.00}"))
    //            .andExpect(status().isBadRequest())
    //            .andExpect(content().string("Insufficient funds for withdrawal."));
    // }

    @Test
    public void testAddStocks() throws Exception {
        mockMvc.perform(post("/portfolios/1/stocks/add")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"AAPL\": 10, \"GOOGL\": 5}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.stocks.AAPL").value(15))
               .andExpect(jsonPath("$.stocks.GOOGL").value(10));
    }

    @Test
    public void testRemoveStocks() throws Exception {
        mockMvc.perform(post("/portfolios/1/stocks/remove")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"AAPL\": 5}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.stocks.AAPL").value(5));
    }
}
