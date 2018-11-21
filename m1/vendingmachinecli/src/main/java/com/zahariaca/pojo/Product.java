package com.zahariaca.pojo;

import com.google.gson.annotations.Expose;

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
    private String supplierId;

    public Product(String name, String description, float price, String supplierId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.uniqueId = idGenerator.incrementAndGet();
        this.supplierId = supplierId;
    }

    public Product(String name, String description, float price, int uniqueId, String supplierId) {
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

    public String getSupplierId() {
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

        if (Float.compare(product.getPrice(), getPrice()) != 0) return false;
        if (getName() != null ? !getName().equals(product.getName()) : product.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(product.getDescription()) : product.getDescription() != null)
            return false;
        return getSupplierId() != null ? getSupplierId().equals(product.getSupplierId()) : product.getSupplierId() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getPrice() != +0.0f ? Float.floatToIntBits(getPrice()) : 0);
        result = 31 * result + (getSupplierId() != null ? getSupplierId().hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Product o) {
        return Integer.compare(this.getUniqueId(), o.getUniqueId());
    }
}
