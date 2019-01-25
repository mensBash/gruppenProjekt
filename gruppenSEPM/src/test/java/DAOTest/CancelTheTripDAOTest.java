package DAOTest;


import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCBookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCRouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CancelTheTripDAOTest {

    private static Connection connection;
    private static BookingDAO dao;
    private static RouteDAO routeDao;
    private static JDBCConnectionManager jdbcConnectionManager = Mockito.mock(JDBCConnectionManager.class);

    private static final String SELECT_BOOKING_ENTRIES = "Select * from bookingEntry where bid = 1";
    private static final String SELECT_BOOKING = "Select * from booking where bid = 1";
    private static final String CONNECTION_URL = "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createForTest.sql'";

    private static Booking booking;

    @BeforeClass
    public static void setUp() throws SQLException, PersistenceException {
        if (connection == null) {
            connection = DriverManager.getConnection(CONNECTION_URL, "sa", "");
        }
        Mockito.when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        dao = new JDBCBookingDAO(jdbcConnectionManager);
        routeDao = new JDBCRouteDAO(jdbcConnectionManager);

        booking = new Booking();
        booking.setStatus(Booking.Status.COMPLETED);
        booking.setPrice(250.0);
        booking.setBookingNr(10000000L);
        City startCity = new City("Vienna", "Austria");
        City endCity = new City("Berlin", "Germany");
        Route route = new Route(startCity, endCity, "120C", 120.0, 53, 450.5);
        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        BookingEntry bookingEntry = new BookingEntry();
        bookingEntry.setRoute(route);
        bookingEntry.setSeatNo(25);
        bookingEntry.setCustomer(customer);
        List<BookingEntry> list = new ArrayList<>();
        list.add(bookingEntry);
        booking.setEntries(list);

        routeDao.createCities(route,true);
        routeDao.createCities(route, false);
        routeDao.createRoute(route);

        dao.createBooking(booking);
        dao.createBookingEntry(booking);

    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }



    @Test
    public void deleteCancelledBookingTest() throws PersistenceException, SQLException {
        PreparedStatement preparedStatement1 = connection.prepareStatement(SELECT_BOOKING);
        PreparedStatement preparedStatement2 = connection.prepareStatement(SELECT_BOOKING_ENTRIES);

        ResultSet rs1 = preparedStatement1.executeQuery();
        ResultSet rs2 = preparedStatement2.executeQuery();
        Assert.assertTrue(rs1.next());
        Assert.assertTrue(rs2.next());

        dao.deleteBooking(booking);

        ResultSet rs3 = preparedStatement1.executeQuery();
        ResultSet rs4 = preparedStatement2.executeQuery();
        Assert.assertFalse(rs3.next());
        Assert.assertFalse(rs4.next());
    }


}
