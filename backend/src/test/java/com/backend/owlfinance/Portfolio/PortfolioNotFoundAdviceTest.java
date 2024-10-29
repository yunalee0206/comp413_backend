import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioNotFoundAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInsufficientFundsHandler() throws Exception {
        mockMvc.perform(post("/portfolios/1/withdraw")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"amount\": 10000.00}"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("Insufficient funds in the portfolio."));
    }
}
