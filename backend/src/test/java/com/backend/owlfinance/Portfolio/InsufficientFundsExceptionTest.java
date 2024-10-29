import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InsufficientFundsExceptionTest {

    @Test
    public void testExceptionMessage() {
        InsufficientFundsException exception = new InsufficientFundsException();
        assertEquals("Insufficient funds in the portfolio.", exception.getMessage());
    }
}
