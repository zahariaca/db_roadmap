package com.zahariaca;

import com.zahariaca.exceptions.IncorrectModeException;
import com.zahariaca.mode.DatabaseOperations;
import com.zahariaca.mode.FileOperations;
import com.zahariaca.mode.OperationMode;
import com.zahariaca.mode.Operations;
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

    //TODO: cli db url, username, password

    public static void main(String[] args) {
        Main main;
        Operations operations;

        try {
            main = CommandLine.populateCommand(new Main(), args);

            if (main.help) {
                CommandLine.usage(main, System.out, CommandLine.Help.Ansi.AUTO);
            }

            logger.log(Level.INFO, ">O: Application startup...");
            System.out.println(String.format("%s%n%s%n%s",
                    "+++++++++++++++++++++++++++++",
                    "+    VENDING MACHINE CLI    +",
                    "+++++++++++++++++++++++++++++"));
            System.out.println("Starting up...");

            if (OperationMode.FILE.getMode().equals(main.mode)) {
                logger.log(Level.INFO, ">O: Starting in file persistence mode.");
                operations = new FileOperations(main.productsFileName, main.usersFileName, main.transactionsFileName);
                operations.startUp();
            } else if (OperationMode.DB.getMode().equals(main.mode)) {
                logger.log(Level.INFO, ">O: Starting in database persistence mode.");
                operations = new DatabaseOperations();
                operations.startUp();
            } else {
                throw new IncorrectModeException(String.format("Unknown mode: %s. Please try again...", main.mode));
            }

        } catch (MissingParameterException | IncorrectModeException e) {
            logger.log(Level.DEBUG, e.getMessage());
            System.err.println(String.format("%s%n", e.getMessage()));
            CommandLine.usage(new Main(), System.out, CommandLine.Help.Ansi.AUTO);
            System.exit(-1);
        }

    }
}
