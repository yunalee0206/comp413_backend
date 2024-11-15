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
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandlePortfolioNotFoundException() throws Exception {
        mockMvc.perform(get("/portfolios/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value("Could not find portfolio for user 99"));
    }

    // TODO: Add back in once working.
    // @Test
    // public void testHandleInsufficientFundsException() throws Exception {
    //     mockMvc.perform(post("/portfolios/1/withdraw")
    //            .contentType(MediaType.APPLICATION_JSON)
    //            .content("{\"amount\": 10000.00}"))
    //            .andExpect(status().isBadRequest())
    //            .andExpect(jsonPath("$.message").value("Insufficient funds for withdrawal"));
    // }

    @Test
    public void testHandleInvalidAmountException() throws Exception {
        mockMvc.perform(post("/portfolios/1/deposit")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"amount\": -50.00}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Deposit amount must be positive"));
    }
}
