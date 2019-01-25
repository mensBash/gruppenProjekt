package DAOTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCRouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphInitTest {

    private static final JDBCConnectionManager jdbcConnectionManager = mock(JDBCConnectionManager.class);

    private static Connection connection;
    private static RouteDAO routeDAO;
    private static Route route;
    private static final String SELECT_ROUTE_STRING = "Select * from route where bus_number = '120C'";
    private static final String SELECT_CITY_STRING = "Select name from cities where name = ?";
    private static final String CONNECTION_URL = "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createForTest.sql'";

    @BeforeClass
    public static void init() throws SQLException, PersistenceException {
        connection = DriverManager.getConnection(CONNECTION_URL, "sa","");
        when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        routeDAO = new JDBCRouteDAO(jdbcConnectionManager);
        City startCity = new City("Berlin", "Germany");
        City endCity = new City("Dresden", "Germany");
        route = new Route(startCity, endCity, "190B", 40.0, 53, 681.0);
        routeDAO.createCities(route, true);
        routeDAO.createCities(route, false);
        route.setRid(36L);
        route.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 17, 5, 0, 0), LocalDateTime.of(2018, 7, 17, 7, 10, 0)));
        routeDAO.createRoute(route);


    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testInitGraph() throws PersistenceException {
         List<Route> routesToLoad = routeDAO.initializeGraph();
        Assert.assertTrue(routesToLoad.contains(route));
    }
}
