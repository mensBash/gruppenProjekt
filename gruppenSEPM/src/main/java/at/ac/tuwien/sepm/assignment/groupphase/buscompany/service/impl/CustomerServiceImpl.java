package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.CustomerDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.CustomerService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private CustomerDAO customerDAO;

    @Autowired
    public CustomerServiceImpl(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public void add(Customer customer) throws ServiceException {
        try {
            customerDAO.add(customer);
        } catch (CustomerException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Customer> loadCustomers() throws ServiceException {
        try {
            return customerDAO.loadCustomers();
        } catch (CustomerException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(Customer c) throws ServiceException {
        try {
            customerDAO.delete(c);
        } catch (CustomerException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(Customer c, String svnr) throws ServiceException{
        try {
            customerDAO.update(c, svnr);
        } catch (CustomerException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Customer checkPremium(String name, String lastName, String socialNumber) throws ServiceException {
        try {
            Customer c = customerDAO.checkPremium(name, lastName, socialNumber);
            if(c == null){
                throw new ServiceException("Customer could not be found");
            }
            else if(!name.equalsIgnoreCase(c.getFirstName()) || !lastName.equalsIgnoreCase(c.getLastName())){
                throw new ServiceException("Incorrect data for given customer");
            }
            else{
                return c;
            }
        } catch (CustomerException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Customer> filterCustomers(Customer customer) throws ServiceException {
        List<Customer> customerList = loadCustomers();
        if(customer==null){
            LOG.error("Invalid parameters");
            throw new ServiceException("Invalid parameters");
        }
        List<Customer> result = new ArrayList<>();

        if(customer.getFirstName() != null && customer.getLastName() != null){
            for(Customer c : customerList){
                if(customer.getFirstName().equals(c.getFirstName()) &&
                    (customer.getLastName().equals(c.getLastName()))){
                    result.add(c);
                }
            }
        }else if(customer.getFirstName() != null){
            for(Customer c : customerList){
                if(customer.getFirstName().equals(c.getFirstName())){
                    result.add(c);
                }
            }
        }else if(customer.getLastName() != null){
            for(Customer c : customerList){
                if(customer.getLastName().equals(c.getLastName())){
                    result.add(c);
                }
            }
        }
        return result;

    }
}
