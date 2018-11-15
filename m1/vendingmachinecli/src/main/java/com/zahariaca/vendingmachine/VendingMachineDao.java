package com.zahariaca.vendingmachine;

import com.zahariaca.dao.Dao;
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
public class VendingMachineDao implements Dao<Product, String> {
    private static final Logger logger = LogManager.getLogger(VendingMachineDao.class);
    private Set<Product> productsSet;

    public VendingMachineDao(Set<Product> products) {
        this.productsSet = products;
    }

    @Override
    public void addProduct(Product product) throws ProductAlreadyExistsException {
        logger.log(Level.DEBUG, ">O: Adding product {} to vending machine", product);
        if (productsSet.stream().noneMatch(product::equals)) {
            productsSet.add(product);
            logger.log(Level.DEBUG, ">O: Successfully added: {} ", product);
        } else {
            throw new ProductAlreadyExistsException(String.format("The product: %n%s%n already exists! Cannot add!", product));
        }
    }

    @Override
    public void displayProducts() {
        productsSet.forEach(p -> System.out.println(p.toString()));
    }

    @Override
    public void deleteProduct(String uniqueId) throws NoSuchProductException {
        logger.log(Level.DEBUG, ">O: Deleting product with id: {} from vending machine", uniqueId);
        Optional<Product> product = productsSet.stream().filter(p -> Integer.valueOf(uniqueId) == p.getUniqueId()).findAny();
        if (product.isPresent()) {
            productsSet.remove(product.get());
            logger.log(Level.DEBUG, ">O: Successfully deleted product with id: {}", uniqueId);
        } else {
            throw new NoSuchProductException(String.format("Product with uniqueID: %s, was not found. Could not be deleted!", uniqueId));
        }
    }

    @Override
    public void changeProduct(Product product) throws NoSuchProductException, ProductAlreadyExistsException {
        logger.log(Level.DEBUG, ">O: Changing product with id: {} to: {} from vending machine", product.getUniqueId(), product);
        Optional<Product> productMatch = productsSet.stream().filter(p -> product.getUniqueId() == p.getUniqueId() && product.getSupplierId().equals(p.getSupplierId())).findAny();

        if (productMatch.isPresent()) {
            productsSet.remove(productMatch.get());
            addProduct(product);
            logger.log(Level.DEBUG, ">O: Successfully changed product with id: {}", product.getUniqueId());
        } else {
            throw new NoSuchProductException(String.format("The product: %s is invalid. No changes are made.", product));
        }
    }

    @Override
    public Product buyProduct(String productName) throws NoSuchProductException {
        logger.log(Level.DEBUG, ">O: Buying product with name: {}",productName);
        Optional<Product> product = productsSet.stream().filter(p -> p.getName().equalsIgnoreCase(productName)).findAny();
        if (product.isPresent()) {
            logger.log(Level.DEBUG, "Successfully bought product, passing to customer.");
            return product.get();
        }
        throw new NoSuchProductException(String.format("Product: %s does not exist.", productName));
    }

    @Override
    public Set<Product> getProductsSet() {
        return productsSet;
    }
}
