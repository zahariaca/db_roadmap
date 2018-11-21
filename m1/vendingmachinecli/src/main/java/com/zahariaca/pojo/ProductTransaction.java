package com.zahariaca.pojo;

import com.google.gson.annotations.Expose;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public class ProductTransaction {
    @Expose
    private final int productUniqueId;
    @Expose
    private final float price;
    @Expose
    private final long date;

    public ProductTransaction(int productUniqueId, float price, long date) {
        this.productUniqueId = productUniqueId;
        this.price = price;
        this.date = date;
    }

    public int getProductUniqueId() {
        return productUniqueId;
    }

    public float getPrice() {
        return price;
    }

    public long getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductTransaction that = (ProductTransaction) o;

        if (getProductUniqueId() != that.getProductUniqueId()) return false;
        if (Float.compare(that.getPrice(), getPrice()) != 0) return false;
        return getDate() == that.getDate();
    }

    @Override
    public int hashCode() {
        int result = getProductUniqueId();
        result = 31 * result + (getPrice() != +0.0f ? Float.floatToIntBits(getPrice()) : 0);
        result = 31 * result + (int) (getDate() ^ (getDate() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ProductTransaction{" +
                "productUniqueId=" + productUniqueId +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
