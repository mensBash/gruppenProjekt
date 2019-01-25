package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;

import java.util.List;

public interface CustomerService {

    /**
     * Adding new customer to the database, propagating to DAO layer
     * @param customer - Customer object to be added
     * @throws ServiceException - thrown when customer cannot be stored
     */
    void add(Customer customer) throws ServiceException;

    /**
     * Loading customer from the database, propagating to DAO layer
     * @return - List of customers stored in the database
     * @throws ServiceException - when customers could not be retrieved for some reason
     */
    List<Customer> loadCustomers() throws ServiceException;

    /**
     * Deletes given customer from the database, propagating to DAO layer
     * @param c - Customer object to be deleted
     * @throws ServiceException - when customer could not be deleted
     */
    void delete(Customer c) throws ServiceException;

    /**
     * Updates customer with the given social security number, propagating to DAO layer
     * @param c - Customer to be updated
     * @param svnr - Social security number of a customer to be updated
     * @throws ServiceException - when customer could not be updated
     */
    void update(Customer c, String svnr) throws ServiceException;

    /**
     * Checks if the customer with given information is premium or not
     * @param name - name of the customer that needs to be checked
     * @param lastName - last name of the customer that needs to be checked
     * @param socialNumber - social security number of the customer that needs to be checked
     * @return Customer object if the customer with provided information is premium, null otherwise
     * @throws ServiceException - when customer could not be found
     */
    Customer checkPremium(String name, String lastName, String socialNumber) throws ServiceException;

    /**
     * Filter customers based on first name, last name or both
     * @param customer - Customer to be found
     * @return - List of customer founded with given specifications
     * @throws ServiceException - when no customer can be found
     */
    List<Customer> filterCustomers(Customer customer) throws ServiceException;
}

