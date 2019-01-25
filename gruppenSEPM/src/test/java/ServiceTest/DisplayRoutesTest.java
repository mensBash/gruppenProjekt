package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.JDBCRouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.RouteServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.validator.ServiceValidator;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayRoutesTest {

    private RouteDAO daoMock = mock(RouteDAO.class);

    private ServiceValidator serviceValidator = new ServiceValidator();
    private Graph graphMock = mock(Graph.class);

    @InjectMocks
    private RouteService service = new RouteServiceImpl(serviceValidator,daoMock,graphMock);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private List<City> cities = new ArrayList<>();
    private List<Route> routes;
    private static DirectedMultigraph<City, Route> graph;

    @Before
    public void init() {
        cities.add(new City("Vienna", "Austria"));
        cities.add(new City("VienNa", "Austria"));
        cities.add(new City("Graz", "Austria"));
        cities.add(new City(null, "Austria"));
        cities.add(new City(null, null));
        cities.add(new City("Berlin", "Germany"));
        cities.add(new City("Munich", "Germany"));
        routes = new ArrayList<>();
        routes.add(new Route(cities.get(0), cities.get(2), "a", 50.0,20,200.0));
        routes.add(new Route(cities.get(2), cities.get(5), "a", 40.0,20,300.0));
        graph = new DirectedMultigraph<City, Route>(Route.class);
        City first = new City("City1", "x");
        City second = new City("City2", "x");
        City third = new City("City3", "x");
        City fourth = new City("City4", "x");
        graph.addVertex(first);
        graph.addVertex(second);
        graph.addVertex(third);
        graph.addVertex(fourth);
        Route route = new Route(first, second, "10", 20.0, 52, 345.5);
        route.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        graph.addEdge(first, second, route);
        Route route1 = new Route(first, third, "10", 20.0, 52, 345.5);
        route1.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        graph.addEdge(first, third, route1);
        Route route2 = new Route(second, third, "10", 20.0, 52, 345.5);
        route2.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        graph.addEdge(second, third, route2);
        Route route3 = new Route(first, second, "10", 20.0, 52, 345.5);
        route3.setTimetable(new Timetable(LocalDateTime.of(2018, 8, 3, 12, 10), LocalDateTime.of(2018, 9, 4, 12, 24)));



    }

   @Test(expected = ServiceException.class)
    public  void calculateDistanceTest() throws ServiceException{
       Assert.assertEquals( service.calculateDistance(routes),500.0,0);
       service.calculateDistance(null);
       Assert.fail();
   }

    @Test(expected = ServiceException.class)
    public  void calculatePriceTest() throws ServiceException{
        Assert.assertEquals( service.calculatePrice(routes),90.0,0);
        service.calculatePrice(null);
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void filterRoutesTest() throws ServiceException{
        Route filter = new Route();
        when(graphMock.getGraph()).thenReturn(graph);
        filter.setStartDestination(new City("City1","x"));
        Assert.assertEquals(service.filterSimpleRoutes(filter).size(),2);
        filter.setStartDestination(null);
        filter.setEndDestination(new City("City3","x"));
        Assert.assertEquals(service.filterSimpleRoutes(filter).size(),2);
        filter.setEndDestination(new City("City4","x"));
        Assert.assertEquals(service.filterSimpleRoutes(filter).size(),0);
        filter.setEndDestination(null);
        filter.setStartDestination(new City("City4","x"));
        Assert.assertEquals(service.filterSimpleRoutes(filter).size(),0);
        service.filterSimpleRoutes(null);
        Assert.fail();

    }

    @Test
    public void getAllSimpleRoutesTest() throws ServiceException{
        when(graphMock.getGraph()).thenReturn(graph);
        Assert.assertEquals(service.getAllSimpleRoutes().size(),3);
    }

    @Test(expected = ServiceException.class)
    public void getAllTimetablesAndPricesRouteNull() throws ServiceException{
        service.loadTimetable(null);
    }

    @Test
    public void getAllTimetableAndPricesValidInput() throws ServiceException, PersistenceException {
        City city1 = new City("Madrid", "Spain");
        City city2 = new City("Athens", "Greece");
        Route tempRoute = new Route(city1, city2, "101", 200.0, 53, 450.5);
        Timetable timetable = new Timetable(LocalDateTime.of(2018,9,22,12,0,0),LocalDateTime.of(2018,9,22,23,30,0));
        tempRoute.setTimetable(timetable);
        Map<Timetable,Double> timetablePriceMap = new HashMap<>();
        timetablePriceMap.put(timetable,200.0);
        when(daoMock.loadTimetable(tempRoute)).thenReturn(timetablePriceMap);
        Assert.assertEquals(timetablePriceMap,service.loadTimetable(tempRoute));
        Mockito.verify(daoMock).loadTimetable(tempRoute);
    }
}