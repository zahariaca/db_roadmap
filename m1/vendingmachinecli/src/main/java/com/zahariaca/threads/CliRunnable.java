package com.zahariaca.threads;

import com.zahariaca.cli.PrimaryCli;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class CliRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(CliRunnable.class);
    private final PrimaryCli primaryCli;

    public CliRunnable(PrimaryCli primaryCli) {
        Thread.currentThread().setName("CliThread");
        this.primaryCli = primaryCli;
        logger.log(Level.INFO, ">O: instantiated {}", Thread.currentThread().getName());
    }


    @Override
    public void run() {
        primaryCli.promptUserOptions();
    }

}
