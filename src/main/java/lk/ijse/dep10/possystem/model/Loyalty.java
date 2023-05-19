package lk.ijse.dep10.possystem.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Loyalty implements Serializable {
    private String customerName;
    private int billNumber;
    private LocalDateTime billDate;
    private BigDecimal billValue;

    public Loyalty() {
    }

    public Loyalty(String customerName, int billNumber, LocalDateTime billDate, BigDecimal billValue) {
        this.customerName = customerName;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.billValue = billValue;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public BigDecimal getBillValue() {
        return billValue;
    }

    public void setBillValue(BigDecimal billValue) {
        this.billValue = billValue;
    }
}
