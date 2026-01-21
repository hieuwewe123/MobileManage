package ra.business.impl;

import ra.business.ICustomerService;
import ra.dao.ICustomerDAO;
import ra.dao.impl.CustomerDAOImpl;
import ra.model.Customer;

import java.util.List;

public class CustomerServiceImpl implements ICustomerService {

    private final ICustomerDAO customerDAO = new CustomerDAOImpl();

    public CustomerServiceImpl(CustomerDAOImpl customerDAO) {
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    @Override
    public Customer findById(int id) {
        return customerDAO.getCustomerById(id);
    }

    @Override
    public boolean addCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            return false;
        }
        return customerDAO.addCustomer(customer);
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customer.getId() <= 0) return false;
        return customerDAO.updateCustomer(customer);
    }

    @Override
    public boolean deleteCustomer(int id) {
        if (id <= 0) return false;
        return customerDAO.deleteCustomer(id);
    }

    @Override
    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDAO.searchCustomers(keyword.trim());
    }
}