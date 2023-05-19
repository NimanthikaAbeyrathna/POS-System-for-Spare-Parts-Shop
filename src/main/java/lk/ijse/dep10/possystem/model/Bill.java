package lk.ijse.dep10.possystem.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Bill implements Serializable {
    private int billNumber;
    private LocalDateTime dateTime;
    private String cashierName;
    private BigDecimal totalPrice;
    private  BigDecimal cash;
    private BigDecimal balance;
    private ArrayList<BillDescription> billDescription = new ArrayList<>();


    public Bill() {
    }

    public Bill(int billNumber, LocalDateTime dateTime, String cashierName,
                BigDecimal totalPrice, BigDecimal cash, BigDecimal balance,
                ArrayList<BillDescription> billDescription) {
        this.billNumber = billNumber;
        this.dateTime = dateTime;
        this.cashierName = cashierName;
        this.totalPrice = totalPrice;
        this.cash = cash;
        this.balance = balance;
        this.billDescription = billDescription;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public ArrayList<BillDescription> getBillDescription() {
        return billDescription;
    }

    public void setBillDescription(ArrayList<BillDescription> billDescription) {
        this.billDescription = billDescription;
    }
}
