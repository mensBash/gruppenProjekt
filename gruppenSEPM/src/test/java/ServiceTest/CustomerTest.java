package ServiceTest;


import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.CustomerDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.CustomerService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.CustomerServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerTest {

    private CustomerDAO customerDAO = mock(CustomerDAO.class);
    private static Customer customer;
    private static Customer customer2;
    private static Customer customer3;
    private CustomerService customerService = new CustomerServiceImpl(customerDAO);

    @BeforeClass
    public static void setUp(){
        customer = new Customer("Mark", "Bolat", "00000100", "amm@gmail.com", LocalDate.of(1996, 8, 10));
        customer2 = new Customer("Mark", "Anthony", "010010", "mark@anthony.com", LocalDate.of(1989, 12, 5));
        customer3 = new Customer("Christof", "Anthony", "oaoaoao", "chris@hotmail.com", LocalDate.of(1995, 5, 4));
    }

    @Test
    public void testAddCustomer() throws CustomerException, ServiceException {
        customerService.add(customer);
        Mockito.verify(customerDAO).add(customer);
    }

    @Test
    public void testUpdateCustomer() throws CustomerException, ServiceException {
        customerService.update(customer, "svnr");
        Mockito.verify(customerDAO).update(customer, "svnr");
    }

    @Test
    public void testDeleteCustomer() throws CustomerException, ServiceException {
        customerService.delete(customer);
        Mockito.verify(customerDAO).delete(customer);
    }

    @Test
    public void testLoadCustomer() throws CustomerException, ServiceException {
        List<Customer> customerList = new ArrayList();
        customerList.add(customer);
        when(customerDAO.loadCustomers()).thenReturn(customerList);
        customerService.loadCustomers();
        Mockito.verify(customerDAO).loadCustomers();
    }

    @Test (expected = ServiceException.class)
    public void testInvalidPremiumCustomer() throws CustomerException, ServiceException {
        when(customerDAO.checkPremium(customer.getFirstName(), customer.getLastName(), customer.getSocialNumber())).thenReturn(null);
        customerService.checkPremium(customer.getFirstName(), customer.getLastName(), customer.getSocialNumber());
        Mockito.verify(customerDAO).checkPremium(customer.getFirstName(), customer.getLastName(), customer.getSocialNumber());
    }

    @Test
    public void testSearechByNameAndLastName() throws CustomerException, ServiceException {
        List<Customer> customerList = new ArrayList<>();
        List<Customer> result = new ArrayList<>();
        customerList.add(customer);
        customerList.add(customer2);
        customerList.add(customer3);
        when(customerDAO.loadCustomers()).thenReturn(customerList);
        result = customerService.filterCustomers(customer);
        Assert.assertEquals(1, result.size());
        Mockito.verify(customerDAO).loadCustomers();
    }

    @Test
    public void testSearechByName() throws CustomerException, ServiceException {
        List<Customer> customerList = new ArrayList<>();
        List<Customer> result = new ArrayList<>();
        customerList.add(customer);
        customerList.add(customer2);
        customerList.add(customer3);
        when(customerDAO.loadCustomers()).thenReturn(customerList);
        Customer newCustomer = new Customer("Mark", null, null, null, null);
        result = customerService.filterCustomers(newCustomer);
        Assert.assertEquals(2, result.size());
        Mockito.verify(customerDAO).loadCustomers();
    }

    @Test
    public void testSearechByLastName() throws CustomerException, ServiceException {
        List<Customer> customerList = new ArrayList<>();
        List<Customer> result = new ArrayList<>();
        customerList.add(customer);
        customerList.add(customer2);
        customerList.add(customer3);
        when(customerDAO.loadCustomers()).thenReturn(customerList);
        Customer newCustomer = new Customer(null, "Anthony", null, null, null);
        result = customerService.filterCustomers(newCustomer);
        Assert.assertEquals(2, result.size());
        Mockito.verify(customerDAO).loadCustomers();
    }

}
