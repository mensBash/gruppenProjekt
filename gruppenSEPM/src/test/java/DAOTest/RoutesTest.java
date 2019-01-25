package DAOTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCRouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.TicketCounter;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoutesTest {

    private static final JDBCConnectionManager jdbcConnectionManager = mock(JDBCConnectionManager.class);

    private static Connection connection;
    private static RouteDAO routeDAO;
    private static Route route;
    private static final String SELECT_ROUTE_STRING = "Select * from route where bus_number = '120C'";
    private static final String SELECT_ROUTE_TIMETABLE_STRING = "Select * from route where bus_number = '140C'";
    private static final String SELECT_CITY_STRING = "Select name from cities where name = ?";
    private static final String CONNECTION_URL = "jdbc:h2:~/databaseTest;INIT=RUNSCRIPT FROM 'classpath:sql/createAndInsert.sql'";

    @BeforeClass
    public static void init() throws SQLException, PersistenceException {
        connection = DriverManager.getConnection(CONNECTION_URL, "sa", "");
        when(jdbcConnectionManager.getConnection()).thenReturn(connection);
        routeDAO = new JDBCRouteDAO(jdbcConnectionManager);
        City startCity = new City("Vienna", "Austria");
        City endCity = new City("Berlin", "Germany");
        route = new Route(startCity, endCity, "120C", 120.0, 53, 450.5);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void createValidRoute() throws SQLException, PersistenceException {
        routeDAO.createRoute(route);
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROUTE_STRING);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Assert.assertEquals("120C", rs.getString("bus_number"));
        }

        rs.close();
        preparedStatement.close();
    }

    @Test(expected = PersistenceException.class)
    public void createEmptyRoute() throws PersistenceException {
        Route route = new Route();
        routeDAO.createRoute(route);
    }

    @Test(expected = PersistenceException.class)
    public void createNullRoute() throws PersistenceException {
        Route route = null;
        routeDAO.createRoute(route);
    }

    @Test
    public void createValidStartDestination() throws PersistenceException, SQLException {
        ResultSet resultSet;
        City city1 = new City("Paris", "France");
        City city2 = new City("Lisbon", "Portugal");
        Route tempRoute = new Route(city1, city2, "100", 330.44, 53, 280.09);
        routeDAO.createCities(tempRoute, true);
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CITY_STRING);
        preparedStatement.setString(1, "Paris");
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Assert.assertEquals("Paris", resultSet.getString("name"));
        }

        resultSet.close();
        preparedStatement.close();
    }

    @Test
    public void createValidEndDestination() throws PersistenceException, SQLException {
        ResultSet resultSet;
        City city1 = new City("Paris", "France");
        City city2 = new City("Lisbon", "Portugal");
        Route tempRoute = new Route(city1, city2, "100", 330.44, 53, 280.09);
        routeDAO.createCities(tempRoute, false);
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CITY_STRING);
        preparedStatement.setString(1, "Lisbon");
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Assert.assertEquals("Lisbon", resultSet.getString("name"));
        }

        resultSet.close();
        preparedStatement.close();
    }

    @Test(expected = NullPointerException.class)
    public void createCityWhenRouteNull() throws PersistenceException {
        Route route = null;
        routeDAO.createCities(route, true);
    }

    @Test(expected = NullPointerException.class)
    public void createCityWhenRouteEmpty() throws PersistenceException {
        Route route = new Route();
        routeDAO.createCities(route, false);
    }

    @Test
    public void loadTimetableValid() throws PersistenceException {
        City city1 = new City("Madrid", "Spain");
        City city2 = new City("Athens", "Greece");
        Route tempRoute = new Route(city1, city2, "101", 200.0, 53, 450.5);
        routeDAO.createCities(tempRoute, true);
        routeDAO.createCities(tempRoute, false);
        Timetable timetable = new Timetable(LocalDateTime.of(2018, 9, 22, 12, 0, 0), LocalDateTime.of(2018, 9, 22, 23, 30, 0));
        tempRoute.setTimetable(timetable);
        routeDAO.createRoute(tempRoute);
        Map<Timetable, Double> timetablePriceMap = routeDAO.loadTimetable(tempRoute);
        for (Map.Entry<Timetable, Double> entry : timetablePriceMap.entrySet()) {
            Assert.assertEquals(timetable, entry.getKey());
            Assert.assertEquals(Double.valueOf(200.0), entry.getValue());
        }
    }

    @Test(expected = PersistenceException.class)
    public void updateRouteRouteNull() throws PersistenceException {
        routeDAO.updateRoute(null);
    }

    @Test
    public void updateRouteValidInput() throws PersistenceException, SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROUTE_STRING);
        routeDAO.createRoute(route);
        route.setDistance(400.0);
        routeDAO.updateRoute(route);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Assert.assertEquals(400.0, rs.getDouble("distance"), 0);
        }

        rs.close();
        preparedStatement.close();
    }

    @Test(expected = PersistenceException.class)
    public void updateTimetableRouteNull() throws PersistenceException {
        routeDAO.updateTimetable(null, new HashMap<>());
    }

    @Test
    public void updateTimetableValidInput() throws PersistenceException, SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROUTE_TIMETABLE_STRING);
        City startCity = new City("Moscow", "Russia");
        City endCity = new City("Sofia", "Bulgaria");
        Route routeTemp = new Route(startCity, endCity, "140C", 200.0, 53, 600.0);
        routeTemp.setTimetable(new Timetable(LocalDateTime.of(2018, 9, 22, 12, 0, 0), LocalDateTime.of(2018, 9, 22, 23, 30, 0)));
        routeDAO.createCities(routeTemp, true);
        routeDAO.createCities(routeTemp,false);
        routeDAO.createRoute(routeTemp);
        Map<Timetable, Double> timetableList = new HashMap<>();
        timetableList.put(new Timetable(LocalDateTime.of(2018, 10, 30, 12, 0, 0), LocalDateTime.of(2018, 10, 31, 11, 30, 0)), 300.0);
        routeDAO.updateTimetable(routeTemp, timetableList);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Assert.assertEquals(LocalDateTime.of(2018, 10, 30, 12, 0, 0), rs.getTimestamp("departure_time").toLocalDateTime());
            Assert.assertEquals(LocalDateTime.of(2018, 10, 31, 11, 30, 0), rs.getTimestamp("arrival_time").toLocalDateTime());
            Assert.assertEquals(300.0, rs.getDouble("price"), 0);
        }

        rs.close();
        preparedStatement.close();
    }

}
