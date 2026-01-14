package ra.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL="jdbc:postgresql://localhost:5432/mobile_management";
    private static final String USER_NAME="postgres";
    private static final String PASSWORD="123456";
    //1. Phương thúc mở kết nối --> Connection
    public static Connection openConnection(){
        Connection conn=null;
        try {
            conn= DriverManager.getConnection(URL,USER_NAME,PASSWORD);
        }catch (Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection conn=openConnection();
        if(conn!=null){
            System.out.println("Connected to the database");
        }else {
            System.out.println("Connection Failed");
        }
    }
    //2. Đóng các kết nối khi làm việc xong
    public static void closeConnection(Connection conn, CallableStatement callableStatement){
        if(conn!=null){
            try {
                conn.close();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        }
        if(callableStatement!=null){
            try {
                callableStatement.close();
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        }
    }
}
