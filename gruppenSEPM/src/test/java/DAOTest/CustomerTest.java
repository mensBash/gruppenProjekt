package DAOTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.CustomerDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCCustomerDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CustomerTest {


    private static JDBCConnectionManager jdbcConnectionManager = Mockito.mock(JDBCConnectionManager.class);
    private static CustomerDAO customerDAO;
    private static Connection connection;
    private static Customer testCustomer;
    private static final String CONNECTION_URL =
        "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createForTest.sql'";
    private static final String SELECT_CUSTOMERS = "Select * from Customer where social_number='020202'";
    private static final String INSERT_QUERY = "INSERT INTO CUSTOMER(name, last_name, social_number, email, birthday) VALUES ('Ammar', 'Voloder', 'socialNum', 'email@at.com', '1996-10-10')";



    @BeforeClass
    public static void setUp() throws SQLException, PersistenceException {
        connection = DriverManager.getConnection(CONNECTION_URL, "sa", "");
        Mockito.when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        customerDAO = new JDBCCustomerDAO(jdbcConnectionManager);
        testCustomer = new Customer("Test", "Customer", "020202", "email@at.com", LocalDate.of(1995, 10, 10));
    }

    @Test
    public void testAddCustomer() throws CustomerException, SQLException {
        customerDAO.add(testCustomer);
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMERS);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()){
            Assert.assertEquals("Test", rs.getString("name"));
        }
    }

    @Test
    public void testUpdateCustomer() throws CustomerException, SQLException {
        testCustomer.setFirstName("NewName");
        customerDAO.update(testCustomer, testCustomer.getSocialNumber());
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMERS);
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()){
            Assert.assertEquals("NewName", rs.getString("name"));
        }
    }

    @Test
    public void testLoadCustomer() throws CustomerException, SQLException {
        List<Customer> customerList = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY);
        preparedStatement.executeUpdate();
        customerList = customerDAO.loadCustomers();
        Assert.assertEquals(1, customerList.size());
    }

    @Test
    public void testDeleteCustomer() throws CustomerException, SQLException {
        customerDAO.delete(testCustomer);
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMERS);
        ResultSet rs = preparedStatement.executeQuery();
        Assert.assertFalse(rs.next());
    }
}
