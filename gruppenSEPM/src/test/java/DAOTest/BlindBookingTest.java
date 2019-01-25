package DAOTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCBookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCRouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.*;
import org.mockito.Mockito;

import javax.sound.midi.Soundbank;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

public class BlindBookingTest {

    private Route route;
    private City vienna;
    private City berlin;
    private City basel;

    private static JDBCConnectionManager jdbcConnectionManager = Mockito.mock(JDBCConnectionManager.class);
    private static BookingDAO bookingDAO;
    private static RouteDAO routeDAO;
    private static Connection connection;
    private static final String CONNECTION_URL =
        "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createForTest.sql'";



    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Before
    public void setUp() throws PersistenceException, SQLException {

        connection = DriverManager.getConnection(CONNECTION_URL, "sa","");
        Mockito.when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        bookingDAO = new JDBCBookingDAO(jdbcConnectionManager);
        routeDAO = new JDBCRouteDAO(jdbcConnectionManager);
        berlin = new City("Berlin","Germany");
        basel = new City("Basel", "Switzerland");
        vienna = new City("Vienna", "Austria");
        berlin.setBlindBooking(true);


        Route r = new Route(vienna, berlin, "12A", 20.0, 52, 345.5);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4),LocalDateTime.now().plusDays(4).plusHours(2)));
        routeDAO.createCities(r,true);
        routeDAO.createCities(r,false);
        routeDAO.createRoute(r);


        r = new Route(vienna, basel, "150A", 20.0, 52, 345.5);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4).plusMinutes(10),LocalDateTime.now().plusDays(4).plusHours(2)));
        routeDAO.createCities(r,false);
        routeDAO.createRoute(r);

    }

    @After
    public void closeUp() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }


    @Test
    public void getAllBuses_ShouldNotBeEmpty() throws PersistenceException {
        List<String> buses = routeDAO.getAllBuses();
        Assert.assertTrue(!buses.isEmpty());
    }

    @Test
    public void getSpecificCityName_shouldFail() throws PersistenceException {
        List<String> cities = routeDAO.getAllCitiesNames();
        Assert.assertTrue(!cities.contains("Graz"));

    }

    @Test
    public void getSpecificCityName_shouldNotFail() throws PersistenceException {
        List<String> cities = routeDAO.getAllCitiesNames();
        Assert.assertTrue(cities.contains("Vienna"));

    }

    @Test
    public void getAllCountries_shouldNotBeEmpty() throws PersistenceException {
        List<String> countriesName = routeDAO.getAllCountriesName();
        Assert.assertTrue(countriesName.contains("Germany"));
    }


    @Test
    public void getAllCities_shouldNotBeEmpty() throws PersistenceException {
        List<City> cities = routeDAO.getAllCities();
        Assert.assertTrue(!cities.isEmpty());
    }

    @Test
    public void modifyBlindBookingCities_shouldNotFail() throws PersistenceException {
        berlin.setBlindBooking(false);
        vienna.setBlindBooking(true);
        bookingDAO.updateCitiesBlindBooking(Arrays.asList(vienna,berlin));

        List<City> cities = routeDAO.getAllCities();
        for (City c :
            cities) {
            if (c.getName().equalsIgnoreCase("vienna")){
                Assert.assertTrue(c.isBlindBooking());
                break;
            }
        }
    }
}
