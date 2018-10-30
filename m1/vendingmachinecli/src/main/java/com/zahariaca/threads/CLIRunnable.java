package com.zahariaca.threads;

import com.zahariaca.exceptions.UnknownUserException;
import com.zahariaca.users.LoginHandler;
import com.zahariaca.users.TypeOfUser;
import com.zahariaca.users.UserFactory;
import com.zahariaca.utils.UserInputUtils;
import com.zahariaca.vendingmachine.events.VendingMachineEvent;
import com.zahariaca.vendingmachine.events.VendingMachineOperations;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class CLIRunnable implements Runnable {
    private Logger logger = LogManager.getLogger(CLIRunnable.class);
    private BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue;

    public CLIRunnable(BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue) {
        this.commandQueue = commandQueue;
    }


    @Override
    public void run() {
        promptForUserIdentification();

//        try {
//            for (int i = 0; i < 9; i++) {
//                if (i % 2 == 0) {
//                    commandQueue.put(new VendingMachineEvent<>() {
//                        @Override
//                        public VendingMachineOperations getType() {
//                            return VendingMachineOperations.ADD;
//                        }
//
//                        @Override
//                        public String getPayload() {
//                            return "ADD TO PRODUCTS - payload";
//                        }
//                    });
//                } else {
//                    commandQueue.put(new VendingMachineEvent<>() {
//                        @Override
//                        public VendingMachineOperations getType() {
//                            return VendingMachineOperations.DELETE;
//                        }
//
//                        @Override
//                        public String getPayload() {
//                            return "DELETE FROM PRODUCTS - payload";
//                        }
//                    });
//                }
//            }
//
//            commandQueue.put(new VendingMachineEvent<>() {
//                @Override
//                public VendingMachineOperations getType() {
//                    return VendingMachineOperations.QUIT;
//                }
//
//                @Override
//                public String getPayload() {
//                    return null;
//                }
//            });
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private void promptForUserIdentification() {
        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        try {
            while (continueCondition) {
                System.out.println(
                        String.format(
                                UserInputUtils.constructPromptMessage(
                                        "%nSelect an operation:%n",
                                        "   [1] Login as Customer. %n",
                                        "   [2] Login as Supplier. %n",
                                        "   [q/quit] to end process. %n")));

                continueCondition = handleUserInput(scanner.next());
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private boolean handleUserInput(String userInput) throws InterruptedException {
        if (UserInputUtils.checkQuitCondition(userInput)) {
            commandQueue.put(new VendingMachineEvent<VendingMachineOperations, String>() {
                @Override
                public VendingMachineOperations getType() {
                    return VendingMachineOperations.QUIT;
                }

                @Override
                public String getPayload() {
                    return null;
                }
            });
            return false;
        }

        try {
            if (Integer.valueOf(userInput) == 1) {
                // no login required for customers, just handle input from them
                UserFactory.getUser(TypeOfUser.CUSTOMER).promptUserOptions(commandQueue);
                return true;
            }

            if (Integer.valueOf(userInput) == 2 && LoginHandler.INSTANCE.checkUserCredentials(TypeOfUser.SUPPLIER)) {
                // check username and password for supplier, then handle input from them
                UserFactory.getUser(TypeOfUser.SUPPLIER).promptUserOptions(commandQueue);
            } else {
                logger.log(Level.ERROR, "Incorrect credentials, try again!");
            }

        } catch (UnknownUserException uex) {
            logger.log(Level.ERROR, uex.getMessage());
        }

        return true;
    }
}
