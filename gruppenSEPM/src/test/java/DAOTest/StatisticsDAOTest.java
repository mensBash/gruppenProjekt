package DAOTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCBookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCRouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsDAOTest {

    private static final JDBCConnectionManager jdbcConnectionManager = mock(JDBCConnectionManager.class);

    private static Connection connection;
    private static BookingDAO bookingDAO;
    private static RouteDAO routeDAO;
    private static Route route;
    private static final String CONNECTION_URL = "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createAndInsert.sql'";

    @BeforeClass
    public static void init() throws SQLException, PersistenceException {
        connection = DriverManager.getConnection(CONNECTION_URL, "sa","");
        when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        bookingDAO = new JDBCBookingDAO(jdbcConnectionManager);
        routeDAO = new JDBCRouteDAO(jdbcConnectionManager);
        route = new Route();
        route.setStartDestination(new City("Vienna", ""));
        route.setEndDestination(new City("Berlin", ""));
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void returnPriceForBookedRoute() throws PersistenceException {
        int price = bookingDAO.getPriceForRoute(route, LocalDate.of(2018,7,29), LocalDate.of(2018,7,30));
        Assert.assertTrue(price > 0);
    }

    @Test
    public void returnZeroPriceForBookedRoute() throws PersistenceException {
        int price = bookingDAO.getPriceForRoute(route, LocalDate.of(2008,7,29), LocalDate.of(2008,7,30));
        Assert.assertEquals(0, price);
    }

    @Test
    public void returnSeatsForBookedRoute() throws PersistenceException {
        int seats = bookingDAO.getNumberOfBookedSeats(LocalDate.of(2018,7,30));
        Assert.assertTrue(seats > 0);
    }

    @Test
    public void returnZeroSeatsForBookedRouts() throws PersistenceException {
        int seats = bookingDAO.getNumberOfBookedSeats(LocalDate.of(2008,7,30));
        Assert.assertEquals(0, seats);
    }

    //NOTE: Test is based on insert in route table in database. If the number of routes is changed, the test will fail
    @Test
    public void getFutureRoutesTest() throws PersistenceException {
        int numberOfFutureRoutes = routeDAO.getNumberAllFutureRoutes();
        Assert.assertEquals(208, numberOfFutureRoutes);
    }

    //NOTE: Test is based on insert in route, booking and bookingEntry table in database. Changes in these could make the test fail (current values - Vienna -- Berlin -- 12 -- 12).
    @Test
    public void getNumberOfTicketsPerRouteTest() throws PersistenceException {
        List<TicketCounter> list = routeDAO.getNumberOfTicketsPerRoute(LocalDateTime.of(2018, 7, 20, 0, 0, 0), LocalDateTime.of(2018, 12, 11, 0, 0, 0));
        Assert.assertEquals("Vienna", list.get(0).getDepartureCity());
        Assert.assertEquals("Berlin", list.get(0).getArrivalCity());
        Assert.assertEquals(12, list.get(0).getOneWayTicketsCounter());
        Assert.assertEquals(12, list.get(0).getReturnTicketsCounter());
    }

    //NOTE: Test is based on insert in booking table in database. If the number of bookings is changed, the test will fail (current value - 14)
    @Test
    public void getFutureBookingsTest() throws PersistenceException {
        int numberOfFutureBookings = bookingDAO.getNumberAllFutureBookings();
        Assert.assertEquals(14, numberOfFutureBookings);
    }

    //NOTE: Test is based on insert in bookingEntry table in database. If the number of booking entries is changed, the test will fail (current value - 55)
    @Test
    public void getFutureBookingEntriesTest() throws PersistenceException {
        int numberOfFutureBookingEntries = bookingDAO.getNumberAllFutureBookingEntries();
        Assert.assertEquals(55, numberOfFutureBookingEntries);
    }

}
