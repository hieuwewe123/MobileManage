package ra.dao;

public interface IAdminDAO {
    boolean authenticate(String username, String password);
}
