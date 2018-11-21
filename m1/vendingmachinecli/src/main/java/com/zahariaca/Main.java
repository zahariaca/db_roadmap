package com.zahariaca;

import com.zahariaca.mode.PersistenceMode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import static picocli.CommandLine.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
@Command(footer = "Copyright(c) Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com), 2018",
        headerHeading = "Usage:%n%n",
        synopsisHeading = "%n",
        optionListHeading = "%nOptions:%n",
        header = "Demo vending machine with persistent data")
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    @Option(names = "-h", description = "Display help/usage.", help = true)
    boolean help;
    @Option(names = {"-m", "--mode"}, description = "Operation Mode, chose between \"file\" and \"db\" persistence", required = true)
    private String mode;
    @Option(names = "-p", defaultValue = "persistence/products.json", description = "Path and name of file where product information is stored. Default: ${DEFAULT-VALUE}")
    private String productsFileName;
    @Option(names = "-u", defaultValue = "persistence/users.json", description = "Path and name of file where user information is stored. Default: ${DEFAULT-VALUE}")
    private String usersFileName;
    @Option(names = "-t", defaultValue = "persistence/transactions.json", description = "Path and name of file where transaction information is stored. Default: ${DEFAULT-VALUE}")
    private String transactionsFileName;

    public static void main(String[] args) {
        Main main;

        try {
            main = CommandLine.populateCommand(new Main(), args);

            if (main.help) {
                CommandLine.usage(main, System.out, CommandLine.Help.Ansi.AUTO);
            }

            if (main.mode.equals("file")) {
                logger.log(Level.INFO, ">O: Application startup...");
                System.out.println(String.format("%s%n%s%n%s",
                        "+++++++++++++++++++++++++++++",
                        "+    VENDING MACHINE CLI    +",
                        "+++++++++++++++++++++++++++++"));

                System.out.println("Starting up...");

                PersistenceMode persistenceMode = new PersistenceMode(main.productsFileName, main.usersFileName, main.transactionsFileName);
                persistenceMode.startUp();
            } else {
                throw new UnsupportedOperationException("DB mode not implemented!");
            }
        } catch (MissingParameterException e) {
            logger.log(Level.DEBUG, e.getMessage());
            System.err.println(String.format("%s%n", e.getMessage()));
            CommandLine.usage(new Main(), System.out, CommandLine.Help.Ansi.AUTO);
            System.exit(-1);
        }

    }
}
