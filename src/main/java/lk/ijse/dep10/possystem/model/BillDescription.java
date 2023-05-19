package lk.ijse.dep10.possystem.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class BillDescription implements Serializable {
    private int billNumber;
    private Long itemCode;
    private String item;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal price;

    public BillDescription() {
    }

    public BillDescription(int billNumber, Long itemCode, String item, BigDecimal unitPrice, int quantity, BigDecimal price) {
        this.billNumber = billNumber;
        this.itemCode = itemCode;
        this.item = item;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.price = price;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getItemCode() {
        return itemCode;
    }

    public void setItemCode(Long itemCode) {
        this.itemCode = itemCode;
    }
}
