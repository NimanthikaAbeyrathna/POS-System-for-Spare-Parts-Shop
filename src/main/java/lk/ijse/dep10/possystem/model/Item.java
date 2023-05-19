package lk.ijse.dep10.possystem.model;

import javafx.scene.control.TextField;

import java.io.Serializable;
import java.math.BigDecimal;

public class Item implements Serializable {
    private Long itemCode;
    private String itemName;
    private BigDecimal sellingPrice;
    private int qty;
    private String consumedQty = "0";
    private BigDecimal price;

    public Item() {
    }

    public Item(Long itemCode, String itemName, BigDecimal sellingPrice) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.sellingPrice = sellingPrice;

    }

    public Item(Long itemCode, String itemName, BigDecimal sellingPrice, int qty, BigDecimal price) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.sellingPrice = sellingPrice;
        this.qty = qty;
        this.price = price;
    }

    public Item(Long itemCode, String itemName, BigDecimal sellingPrice,  String consumedQty, BigDecimal price) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.sellingPrice = sellingPrice;
        this.consumedQty = consumedQty;
        this.price = price;
    }

    public Item(Long itemCode, String itemName, BigDecimal sellingPrice, int qty, String consumedQty, BigDecimal price) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.sellingPrice = sellingPrice;
        this.qty = qty;
        this.consumedQty = consumedQty;
        this.price = price;
    }

    public BigDecimal getPrice(){
        return new BigDecimal(getConsumedQty()).multiply(sellingPrice);
    }

    public String getConsumedQty() {
        return consumedQty;
    }

    public void setConsumedQty(String consumedQty) {
        this.consumedQty = consumedQty;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Long getItemCode() {
        return itemCode;
    }

    public void setItemCode(Long itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
