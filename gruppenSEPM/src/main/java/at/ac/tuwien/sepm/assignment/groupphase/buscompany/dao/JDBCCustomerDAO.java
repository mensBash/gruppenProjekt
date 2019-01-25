package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JDBCCustomerDAO implements CustomerDAO {


    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String CUSTOMER_INSERT_QUERY = "INSERT INTO CUSTOMER(id, name, last_name, social_number, email, birthday) VALUES (default,?,?,?,?,?)";
    private static final String SELECT_CUSTOMERS_QUERY = "SELECT * FROM CUSTOMER";
    private static final String CHECK_CUSTOMERS_QUERY = "SELECT * FROM CUSTOMER WHERE social_number = ?";
    private static final String DELETE_CUSTOMER_QUERY = "DELETE FROM CUSTOMER WHERE social_number = ?";
    private static final String UPDATE_CUSTOMER_QUERY = "UPDATE CUSTOMER SET name=?, last_name=?, email=?, social_number=?, birthday=? WHERE social_number=?";
    private Connection connection;

    @Autowired
    public JDBCCustomerDAO(JDBCConnectionManager jdbcConnectionManager) throws PersistenceException {
        try {
            this.connection = jdbcConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new PersistenceException("Error in database connection", e);
        }
    }

    @Override
    public void add(Customer customer) throws CustomerException {
        LOG.debug("Adding customer to the database");

        try {
            PreparedStatement insertCustomerStatement = connection.prepareStatement(CUSTOMER_INSERT_QUERY);


            insertCustomerStatement.setString(1, customer.getFirstName());
            insertCustomerStatement.setString(2, customer.getLastName());
            insertCustomerStatement.setString(3, customer.getSocialNumber());
            insertCustomerStatement.setString(4, customer.getEmail());
            insertCustomerStatement.setDate(5, Date.valueOf(customer.getBirthday()));
            insertCustomerStatement.executeUpdate();

            insertCustomerStatement.close();
        } catch (SQLException e) {
            throw new CustomerException("Customers could not be added.", e);
        }

    }

    @Override
    public List<Customer> loadCustomers() throws CustomerException {

        List<Customer> customers = new ArrayList<>();
        try {
            PreparedStatement selectCustomersStatement = connection.prepareStatement(SELECT_CUSTOMERS_QUERY);
            ResultSet rs = selectCustomersStatement.executeQuery();

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("name"),
                    rs.getString("last_name"),
                    rs.getString("social_number"),
                    rs.getString("email"),
                    rs.getDate("birthday").toLocalDate()
                ));
            }

            rs.close();
            selectCustomersStatement.close();
        } catch (SQLException e) {
            throw new CustomerException("Customers could not be loaded", e);
        }
        return customers;
    }

    @Override
    public void delete(Customer c) throws CustomerException {

        try {
            PreparedStatement deleteCustomerStatement = connection.prepareStatement(DELETE_CUSTOMER_QUERY);

            deleteCustomerStatement.setString(1, c.getSocialNumber());
            deleteCustomerStatement.executeUpdate();

            deleteCustomerStatement.close();
        } catch (SQLException e) {
            throw new CustomerException("Customer could not be deleted", e);
        }

    }

    @Override
    public void update(Customer c, String svnr) throws CustomerException {
        try {
            PreparedStatement updateCustomerStatement = connection.prepareStatement(UPDATE_CUSTOMER_QUERY);
            updateCustomerStatement.setString(1, c.getFirstName());
            updateCustomerStatement.setString(2, c.getLastName());
            updateCustomerStatement.setString(3, c.getEmail());
            updateCustomerStatement.setString(4, c.getSocialNumber());
            updateCustomerStatement.setDate(5, Date.valueOf(c.getBirthday()));
            updateCustomerStatement.setString(6, svnr);

            updateCustomerStatement.executeUpdate();
            updateCustomerStatement.close();
        } catch (SQLException e) {
            throw new CustomerException("Customer could not be updated.", e);
        }
    }

    @Override
    public Customer checkPremium(String name, String lastName, String socialNumber) throws CustomerException {
        Customer c = null;

        try {
            PreparedStatement checkCustomerStatement = connection.prepareStatement(CHECK_CUSTOMERS_QUERY);
            checkCustomerStatement.setString(1, socialNumber);
            ResultSet rs = checkCustomerStatement.executeQuery();
            while (rs.next()) {
                c = new Customer(
                    rs.getString("name"),
                    rs.getString("last_name"),
                    rs.getString("social_number"),
                    rs.getString("email"),
                    rs.getDate("birthday").toLocalDate()
                );
            }

            rs.close();
            checkCustomerStatement.close();
        } catch (SQLException e) {
            throw new CustomerException("No customer could be found.", e);
        }

        return c;
    }
}
