package lk.ijse.dep10.possystem.model;

import lk.ijse.dep10.possystem.model.util.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@AllArgsConstructor
@Getter
@Setter
public class Item implements Serializable {

    private User role;
    private int batchNo;
    private Long itemCode;
    private String model;
    private String itemName;

    private BigDecimal netPrice;
    private int quantity;
    private BigDecimal discount;
    private Date dateOfBought;
    private BigDecimal sellingPrice;
    private BigDecimal profit;


}
