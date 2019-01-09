package com.zahariaca.pojo;

import com.google.gson.annotations.Expose;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Product implements Comparable<Product> {
    @Expose
    private static AtomicInteger idGenerator = new AtomicInteger(1000);
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private float price;
    @Expose
    private int uniqueId;
    @Expose
    private int supplierId;

    public Product(String name, String description, float price, int supplierId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.uniqueId = idGenerator.incrementAndGet();
        this.supplierId = supplierId;
    }

    public Product(int uniqueId, String name, String description, float price, int supplierId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.uniqueId = uniqueId;
        this.supplierId = supplierId;
    }

    public static void setIdGenerator(AtomicInteger idGenerator) {
        Product.idGenerator = idGenerator;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Float.compare(product.price, price) == 0 &&
                supplierId == product.supplierId &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price, supplierId);
    }

    @Override
    public int compareTo(Product o) {
        return Integer.compare(this.getUniqueId(), o.getUniqueId());
    }
}
