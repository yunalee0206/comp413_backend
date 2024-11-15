import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.backend.owlfinance.StockApplication;

@SpringBootTest(classes = StockApplication.class)
@AutoConfigureMockMvc
public class PortfolioNotFoundAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    // TODO: Add back in once working.
    // @Test
    // public void testInsufficientFundsHandler() throws Exception {
    //     mockMvc.perform(post("/portfolios/1/withdraw")
    //            .contentType(MediaType.APPLICATION_JSON)
    //            .content("{\"amount\": 10000.00}"))
    //            .andExpect(status().isOk())
    //            .andExpect(content().string("Insufficient funds in the portfolio."));
    // }
}
