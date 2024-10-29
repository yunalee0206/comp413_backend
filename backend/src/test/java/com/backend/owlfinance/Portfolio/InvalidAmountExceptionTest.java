import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InvalidAmountExceptionTest {

    @Test
    public void testExceptionMessage() {
        InvalidAmountException exception = new InvalidAmountException("Invalid amount");
        assertEquals("Invalid amount", exception.getMessage());
    }
}
