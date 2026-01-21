package ra.dao.impl;

import ra.dao.IAdminDAO;
import ra.utils.DBUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class AdminDAOImpl implements IAdminDAO {

    @Override
    public boolean authenticate(String username, String password) {
        String sql = "{ ? = call check_admin_login(?, ?) }";

        try (Connection conn = DBUtil.openConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, java.sql.Types.BOOLEAN);
            cs.setString(2, username.trim());
            cs.setString(3, password.trim());
            cs.execute();

            return cs.getBoolean(1);
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
            return false;
        }
    }
}