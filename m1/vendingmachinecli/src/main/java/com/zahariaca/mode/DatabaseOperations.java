package com.zahariaca.mode;

import com.zahariaca.cli.PrimaryCli;
import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.vendingmachine.OperatorInteractions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public class DatabaseOperations implements Operations {
    private final Logger logger = LogManager.getLogger(DatabaseOperations.class);
    private final BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue = new LinkedBlockingQueue<>(1);
    private final BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue = new LinkedBlockingQueue<>(1);
    private final BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue = new LinkedBlockingQueue<>(10);
    private PrimaryCli primaryCli;
    private OperatorInteractions<Product, String[]> vendingMachine;
    private Dao<User, String> usersDao;

    @Override
    public void startUp() {
        throw new UnsupportedOperationException("DB mode not implemented!");
        // TODO: prerequisites, contact DB

        // TODO: create database vending machine dao

        // TODO: create database user dao

        // TODO: create primaryCli

        // TODO: ExecutorService

        //TODO: create VendingMachineRunnable.makeDatabaseVendingMachineRunnable(...) and start

        //TODO: create create CliRunnable, pass primaryCli, and start

        //TODO: executor shutdown
    }
}
