package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.ProductTransaction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class MySqlTransactionsDao implements Dao<ProductTransaction, String> {
    private static final Logger logger = LogManager.getLogger(MySqlTransactionsDao.class);

    @Override
    public Optional<ProductTransaction> get(String id) {
        throw new UnsupportedOperationException("Transaction getting not implemented!");
    }

    @Override
    public Set<ProductTransaction> getAll() {
        throw new UnsupportedOperationException("Transaction getting not implemented!");
    }

    @Override
    public Set<ProductTransaction> getAll(String id) {
        throw new UnsupportedOperationException("Transaction getting not implemented!");
    }

    @Override
    public void save(ProductTransaction productTransaction) {
        try(Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO transactions VALUES (?,?,?)");
            preparedStatement.setInt(1, productTransaction.getSupplierId());
            preparedStatement.setInt(2, productTransaction.getProductUniqueId());
            preparedStatement.setLong(3, productTransaction.getDate());

            preparedStatement.execute();
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method save(...): {}", e.getMessage());
        }
    }

    @Override
    public void update(ProductTransaction productTransaction, String[] params) {
        throw new UnsupportedOperationException("Transaction updating will not be supported!");
    }

    @Override
    public void delete(ProductTransaction productTransaction) {
        throw new UnsupportedOperationException("Transaction updating will not be supported!");
    }
}
