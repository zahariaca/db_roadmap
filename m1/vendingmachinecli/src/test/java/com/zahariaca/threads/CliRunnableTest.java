package com.zahariaca.threads;

import com.zahariaca.cli.PrimaryCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
public class CliRunnableTest {
    private CliRunnable cliRunnable;
    private PrimaryCli primaryCli;

    @BeforeEach
    void init() {
        primaryCli = mock(PrimaryCli.class);
        cliRunnable = new CliRunnable(primaryCli);
    }

    @Test
    public void testLoginAsCustomer() {
        cliRunnable.run();
        verify(primaryCli, times(1)).promptUserOptions();
    }
}
