package com.zahariaca.pojo;

import com.google.gson.annotations.Expose;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public class ProductTransaction {
    @Expose
    private final int supplierId;
    @Expose
    private final int productUniqueId;
    @Expose
    private final long date;

    public ProductTransaction(int supplierId, int productUniqueId, long date) {
        this.supplierId = supplierId;
        this.productUniqueId = productUniqueId;
        this.date = date;
    }

    public int getProductUniqueId() {
        return productUniqueId;
    }

    public int getSupplierId() {
        return supplierId;
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
        if (Integer.compare(that.getSupplierId(), getSupplierId()) != 0) return false;
        return getDate() == that.getDate();
    }

    @Override
    public int hashCode() {
        int result = getProductUniqueId();
        result = 31 * result + (getSupplierId() != +0.0f ? Float.floatToIntBits(getSupplierId()) : 0);
        result = 31 * result + (int) (getDate() ^ (getDate() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ProductTransaction{" +
                "productUniqueId=" + productUniqueId +
                ", price=" + supplierId +
                ", date=" + date +
                '}';
    }
}
