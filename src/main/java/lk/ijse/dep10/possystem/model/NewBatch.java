package lk.ijse.dep10.possystem.model;

import lk.ijse.dep10.possystem.db.DBConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;

@AllArgsConstructor
@Setter
@Getter
public class NewBatch implements Serializable {

    private int supplierId;
    private String supplierName;
    private int batchNo;

    private Date date;

    private BigDecimal total;


//   // private String getsuplierName(){
//        String name="";
//        Connection connection = DBConnection.getInstance().getConnection();
//        String sql="SELECT *FROM Supplier WHERE id=?";
//
//        try {
//            PreparedStatement prd = connection.prepareStatement(sql);
//            prd.setInt(1,getSupplierId());
//            ResultSet rst = prd.executeQuery();
//            if(rst.next()){
//                name = rst.getString("name");
//            }
//            System.out.println("getsupplierName");
//            return name;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
