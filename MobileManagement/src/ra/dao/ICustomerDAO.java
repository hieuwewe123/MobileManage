package ra.dao;

import ra.model.Customer;

import java.util.List;

public interface ICustomerDAO {
    List<Customer> getAllCustomers();
    Customer getCustomerById(int id);
    boolean addCustomer(Customer customer);
    boolean updateCustomer(Customer customer);
    boolean deleteCustomer(int customerId);
    List<Customer> searchCustomers(String keyword);
}