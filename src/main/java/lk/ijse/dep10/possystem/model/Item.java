package lk.ijse.dep10.possystem.model;

<<<<<<< HEAD
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import lk.ijse.dep10.possystem.model.util.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@AllArgsConstructor

public class Item implements Serializable {

    private User role;
    private int batchNo;
    private Long itemCode;
    private String model;
    private String itemName;
    private BigDecimal netPrice;
    private int qty;
    private String consumedQty = "0";
    private BigDecimal price;
    private BigDecimal discount;
    private Date dateOfBought;
    private BigDecimal sellingPrice;
    private BigDecimal profit;


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
>>>>>>> origin/master
}
