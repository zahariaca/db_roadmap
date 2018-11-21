import com.zahariaca.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 21.11.2018
 */
public class MainTest {
    private Main main;

    @BeforeEach
    void init() {
        main = new Main();
    }

    @Test
    void expectUnsupportedOperation() {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->  main.main(new String[] {"--mode", "db"}));
    }

}
