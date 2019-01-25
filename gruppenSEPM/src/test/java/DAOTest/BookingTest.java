package DAOTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCBookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BookingTest {

    private static JDBCConnectionManager jdbcConnectionManager = Mockito.mock(JDBCConnectionManager.class);
    private static BookingDAO bookingDAO;
    private static Connection connection;
    private static final String CONNECTION_URL =
        "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createAndInsert.sql'";
    private static final String SELECT_SEATS = "Select sid from BookingEntry where sid = ?";


    @BeforeClass
    public static void init() throws SQLException, PersistenceException {
        if(connection == null){
            connection = DriverManager.getConnection(CONNECTION_URL, "sa","");
        }
        Mockito.when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        bookingDAO = new JDBCBookingDAO(jdbcConnectionManager);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test (expected = PersistenceException.class)
    public void testBookingNull() throws PersistenceException {
        Booking booking = null;
        bookingDAO.createBooking(booking);
    }

    @Test (expected = PersistenceException.class)
    public void testEmptyBooking() throws PersistenceException {
        Booking booking = new Booking();
        bookingDAO.createBooking(booking);
    }

    @Test (expected = PersistenceException.class)
    public void testPriceNotSet() throws PersistenceException {
        Booking booking = new Booking();
        booking.setBid(1L);
        booking.setStatus(Booking.Status.COMPLETED);
        bookingDAO.createBooking(booking);
    }

    @Test
    public void testValidBooking() throws PersistenceException {
        Booking booking = new Booking();
        booking.setStatus(Booking.Status.COMPLETED);
        booking.setBid(1L);
        booking.setPrice(20.0);
        booking.setBookingNr(System.currentTimeMillis());
        bookingDAO.createBooking(booking);
    }

    @Test
    public void testValidLoadBookingEntries() throws PersistenceException {
        List<BookingEntry> entryList = bookingDAO.loadBookingEntries();
        Assert.assertFalse(entryList.isEmpty());
        Route route = new Route();
        route.setRid(4L);
        BookingEntry bookingEntry = new BookingEntry(null, 24, route);
        Assert.assertTrue(entryList.contains(bookingEntry));
    }

    @Test
    public void testValidLoadBookings() throws PersistenceException {
        List<Booking> bookingList = bookingDAO.loadBookings();
        Assert.assertFalse(bookingList.isEmpty());
        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        Route route = new Route();
        route.setRid(4L);
        BookingEntry bookingEntry = new BookingEntry(customer, 24, route);
        Booking booking = new Booking(1L, Booking.Status.COMPLETED, 40.0);
        Assert.assertTrue(bookingList.get(0).getEntries().contains(bookingEntry));
        Assert.assertTrue(bookingList.contains(booking));
    }
}
