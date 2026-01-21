package ra.business;

import ra.dao.IAdminDAO;
import ra.dao.impl.AdminDAOImpl;

public class AdminService {
    private final IAdminDAO adminDAO = new AdminDAOImpl();

    public AdminService(AdminDAOImpl adminDAO) {
    }

    public boolean login(String username, String password) {
        return adminDAO.authenticate(username, password);
    }
}