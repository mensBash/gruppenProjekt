package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;

import java.util.List;

public interface CustomerDAO {


    /**
     * Adding new customer to the database with the parameters stored in Customer object
     * @param customer instance of customer which contains all information needed to store customer to the database
     * @throws CustomerException - thrown when customer cannot be stored
     */
    void add(Customer customer) throws CustomerException;

    /**
     * Loading all premium customers from the database
     * @return List of premium customers
     * @throws CustomerException - when customers cannot be loaded
     */
    List<Customer> loadCustomers() throws CustomerException;

    /**
     * Deletes given customer from the database
     * @param c - Customer object to be deleted from the database
     * @throws CustomerException - when customer cannot be deleted for some reason
     */
    void delete(Customer c) throws CustomerException;

    /**
     * Updates customer with the given Customer object and social security number
     * @param c - Customer object to be updated
     * @param svnr - Customer's old social number needed in order to find the customer in the database
     * @throws CustomerException - when customer could not be updated
     */
    void update(Customer c, String svnr) throws CustomerException;

    /**
     * Checks if the customer with given name, last name and social security number is premium or not
     * @param name - name of the customer to be checked
     * @param lastName - last name of the customer to be checked
     * @param socialNumber - social security number of customer to be checked
     * @return - Customer object in case that customer with provided information is premium, otherwise null
     * @throws CustomerException - when no customer could be found
     */
    Customer checkPremium(String name, String lastName, String socialNumber) throws CustomerException;
}
