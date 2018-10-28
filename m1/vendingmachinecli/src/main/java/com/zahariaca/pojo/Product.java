package com.zahariaca.pojo;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Product {
    private String name;
    private String description;
    private float price;
    private String uniqueId;

    public Product(String name, String description, float price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.uniqueId = DigestUtils.sha256Hex(name+description+price);
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

    public String getUniqueId() {
        return uniqueId;
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
        if (!getName().equals(product.getName())) return false;
        if (!getDescription().equals(product.getDescription())) return false;
        return getUniqueId().equals(product.getUniqueId());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + (getPrice() != +0.0f ? Float.floatToIntBits(getPrice()) : 0);
        result = 31 * result + getUniqueId().hashCode();
        return result;
    }
}
