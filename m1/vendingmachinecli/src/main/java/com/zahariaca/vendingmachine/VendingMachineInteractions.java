package com.zahariaca.vendingmachine;

import com.zahariaca.dao.Dao;
import com.zahariaca.exceptions.IllegalProductOperation;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.pojo.Product;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class VendingMachineInteractions implements OperatorInteractions<Product, String[]> {
    private static final Logger logger = LogManager.getLogger(VendingMachineInteractions.class);
    private Dao<Product, Integer> vendingMachineDao;

    public VendingMachineInteractions(Dao<Product, Integer> vendingMachineDao) {
        this.vendingMachineDao = vendingMachineDao;
    }

    @Override
    public void displayProducts() {
        vendingMachineDao.getAll().forEach(product -> System.out.println(product.toString()));
    }

    @Override
    public void displayProducts(String[] supplierId) {
        vendingMachineDao.getAll(Integer.valueOf(supplierId[0])).forEach(product -> System.out.println(product.toString()));
    }

    @Override
    public void addProduct(Product product) throws ProductAlreadyExistsException {
        logger.log(Level.DEBUG, ">O: Adding product {} to vending machine", product);
        if (vendingMachineDao.getAll().stream().noneMatch(product::equals)) {
            vendingMachineDao.save(product);
            logger.log(Level.DEBUG, ">O: Successfully added: {} ", product);
        } else {
            throw new ProductAlreadyExistsException(String.format("The product: %n%s%n already exists! Cannot add!", product));
        }
    }

    @Override
    public void deleteProduct(String[] ids) throws NoSuchProductException, IllegalProductOperation {
        logger.log(Level.DEBUG, ">O: Deleting product with id: {} from vending machine", ids[0]);
        Optional<Product> product = vendingMachineDao.get(Integer.valueOf(ids[0]));

        if (product.isPresent()) {
            if (!product.get().getSupplierId().equals(ids[1])) {
                throw new IllegalProductOperation("Product cannot be modified by other suppliers.");
            }

            vendingMachineDao.delete(product.get());
            logger.log(Level.DEBUG, ">O: Successfully deleted product with id: {}", ids[0]);
        } else {
            throw new NoSuchProductException(String.format("Product with uniqueID: %s, was not found. Could not be deleted!", ids[0]));
        }
    }

    @Override
    public void changeProduct(String[] productAttributes) throws NoSuchProductException, IllegalProductOperation {
        int productId = Integer.parseInt(productAttributes[3]);
        String supplierID = productAttributes[4];

        logger.log(Level.DEBUG, ">O: Changing product with id: {} to: {} from vending machine", productId, productAttributes);
        Optional<Product> product = vendingMachineDao.get(productId);

        if (product.isPresent()) {
            if (!product.get().getSupplierId().equals(supplierID)) {
                throw new IllegalProductOperation("Product cannot be modified by other suppliers.");
            }

            vendingMachineDao.update(product.get(), productAttributes);
            logger.log(Level.DEBUG, ">O: Successfully changed product with id: {}", productId);
        } else {
            throw new NoSuchProductException(String.format("The product: %s is invalid. No changes are made.", productAttributes[3]));
        }
    }

    @Override
    public Product buyProduct(String[] productName) throws NoSuchProductException {
        //TODO: user should be given a list of available products an select one of them, this way this method could receive the product ID and not the name
        logger.log(Level.DEBUG, ">O: Buying product with name: {}", productName[0]);
        Optional<Product> product = vendingMachineDao.getAll().stream().filter(p -> p.getName().equalsIgnoreCase(productName[0])).findAny();
        if (product.isPresent()) {
            logger.log(Level.DEBUG, "Successfully bought product, passing to customer.");
            return product.get();
        }
        throw new NoSuchProductException(String.format("Product: %s does not exist.", productName[0]));
    }

    @Override
    public Set<Product> getProductsSet() {
        return vendingMachineDao.getAll();
    }
}
