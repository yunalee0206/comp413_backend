import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandlePortfolioNotFoundException() throws Exception {
        mockMvc.perform(get("/portfolios/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value("Portfolio not found for userId 99"));
    }

    @Test
    public void testHandleInsufficientFundsException() throws Exception {
        mockMvc.perform(post("/portfolios/1/withdraw")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"amount\": 10000.00}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Insufficient funds in the portfolio."));
    }

    @Test
    public void testHandleInvalidAmountException() throws Exception {
        mockMvc.perform(post("/portfolios/1/deposit")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"amount\": -50.00}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Invalid amount"));
    }
}
