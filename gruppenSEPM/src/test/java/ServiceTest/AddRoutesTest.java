package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidRouteException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceValidatorException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.RouteServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.validator.ServiceValidator;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.*;

import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AddRoutesTest {

    private ServiceValidator serviceValidator = new ServiceValidator();
    private RouteDAO daoMock = mock(RouteDAO.class);

    private Graph graphMock = mock(Graph.class);


    @InjectMocks
    private RouteService service = new RouteServiceImpl(serviceValidator,daoMock,graphMock);

    private static Route route;


    private static DirectedMultigraph<City, Route> graph;

    @BeforeClass
    public static void init(){
        graph = new DirectedMultigraph<City, Route>(Route.class);
        City first = new City("Krakow", "Poland");
        City second = new City("Budapest", "Hungary");
        graph.addVertex(first);
        graph.addVertex(second);
        route = new Route(first, second, "10", 20.0, 52, 345.5);
        route.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        graph.addEdge(first, second, route);
    }

    @Before
    public void setUp() {
        route = new Route();
    }

    @After
    public void tearDown(){
        route = null;
    }

    @Test
    public void testGraphDoesNotContainStart() throws InvalidRouteException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        City city = new City("Beograd", "Serbia");
        City city1 = new City("Budapest", "Hungary");
        route = new Route(city, city1, "10", 10.0, 52, 400.0);
        service.addRoute(route);
        Assert.assertTrue(graph.containsVertex(city));
    }

    @Test
    public void testGraphDoesNotContainEnd() throws InvalidRouteException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        City city = new City("Krakow", "Poland");
        City city1 = new City("Zagreb", "Croatia");
        route = new Route(city, city1, "10", 10.0, 52, 400.0);
        service.addRoute(route);
        Assert.assertTrue(graph.containsVertex(city1));
    }


    @Test
    public void testGraphContainsStart() throws InvalidRouteException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        City city = new City("Krakow", "Poland");
        City city1 = new City("Pecs", "Hungary");
        route = new Route(city, city1, "10", 10.0, 52, 400.0);
        service.addRoute(route);
        Assert.assertFalse(graph.addVertex(city));
    }


    @Test
    public void testGraphContainsEnd() throws InvalidRouteException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        City city = new City("Warsaw", "Poland");
        City city1 = new City("Budapest", "Hungary");
        route = new Route(city, city1, "10", 20.0, 52, 345.5);
        service.addRoute(route);
        Assert.assertFalse(graph.addVertex(city));
    }


    @Test (expected = ServiceException.class)
    public void testGraphContainsBoth() throws InvalidRouteException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        City city = new City("Krakow", "Poland");
        City city1 = new City("Budapest", "Hungary");
        route = new Route(city, city1, "10", 20.0, 52, 345.5);
        route.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        service.addRoute(route);
    }

    }