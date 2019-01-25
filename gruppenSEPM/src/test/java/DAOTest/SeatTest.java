package DAOTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCRouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCBookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SeatTest {


    private static JDBCConnectionManager jdbcConnectionManager = Mockito.mock(JDBCConnectionManager.class);
    private static BookingDAO bookingDAO;
    private static RouteDAO routeDAO;
    private static Connection connection;
    private static final String CONNECTION_URL =
        "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createForTest.sql'";
    private static final String SELECT_SEATS = "Select sid from BookingEntry where sid = ?";

    @BeforeClass
    public static void init() throws SQLException, PersistenceException {
        if(connection == null){
            connection = DriverManager.getConnection(CONNECTION_URL, "sa","");
        }
        Mockito.when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        bookingDAO = new JDBCBookingDAO(jdbcConnectionManager);
        routeDAO = new JDBCRouteDAO(jdbcConnectionManager);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void addSeatValid() throws PersistenceException, SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SEATS);
        Booking booking = new Booking(1L, Booking.Status.COMPLETED, 3.0);
        booking.setBookingNr(System.currentTimeMillis());
        bookingDAO.createBooking(booking);
        Route route = new Route();
        route.setRid(1L);
        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Musterman");
        BookingEntry bookingEntry = new BookingEntry(customer, 2, route);
        List<BookingEntry> entryList = new ArrayList<>();
        entryList.add(bookingEntry);
        booking.setEntries(entryList);
        preparedStatement.setLong(1, bookingEntry.getSeatNo());
        bookingDAO.createBookingEntry(booking);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            Assert.assertEquals(2, resultSet.getInt("sid"));
        }
    }

    @Test(expected = PersistenceException.class)
    public void addSeatsSeatNumberNull() throws PersistenceException, SQLException{
        Booking booking = new Booking(1L, Booking.Status.COMPLETED, 3.0);
        bookingDAO.createBooking(booking);
        Route route = new Route();
        route.setRid(1L);
        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Musterman");
        BookingEntry bookingEntry = new BookingEntry(customer, null, route);
        List<BookingEntry> entryList = new ArrayList<>();
        entryList.add(bookingEntry);
        booking.setEntries(entryList);
        bookingDAO.createBookingEntry(booking);
    }

    @Test
    public void loadSeatsTest() throws PersistenceException, SQLException {
        City departure = new City();
        departure.setName("Madrid");
        departure.setCountry("Spain");
        City arrival = new City();
        arrival.setName("Amsterdam");
        arrival.setCountry("Netherlands");
        Route route = new Route();
        route.setStartDestination(departure);
        route.setEndDestination(arrival);
        route.setBusNumber("bus1");
        route.setPrice(50.0);
        route.setDistance(350.0);
        route.setCapacity(54);
        routeDAO.createCities(route,true);
        routeDAO.createCities(route,false);
        routeDAO.createRoute(route);

        Booking booking = new Booking(1L, Booking.Status.COMPLETED, 100.0);
        booking.setBookingNr(System.currentTimeMillis());
        bookingDAO.createBooking(booking);

        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        BookingEntry bookingEntry1 = new BookingEntry(customer,4,route);
        BookingEntry bookingEntry2 = new BookingEntry(customer, 7, route);
        List<BookingEntry> listOfEntries = new ArrayList<>();
        listOfEntries.add(bookingEntry1);
        listOfEntries.add(bookingEntry2);
        booking.setEntries(listOfEntries);
        bookingDAO.createBookingEntry(booking);
        List<Integer> result = routeDAO.loadSeats(route);

        Assert.assertEquals(Integer.valueOf(4),result.get(0));
        Assert.assertEquals(Integer.valueOf(7),result.get(1));
    }

}
