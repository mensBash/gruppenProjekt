package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.RouteServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.validator.ServiceValidator;
import com.itextpdf.kernel.pdf.PdfDocument;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import javax.print.DocPrintJob;
import javax.print.PrintService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphInitTest {

    private ServiceValidator serviceValidator = new ServiceValidator();
    private static DirectedMultigraph<City, Route> graph;
    private static Route route;
    private Graph graphMock = mock(Graph.class);
    private RouteDAO daoMock = mock(RouteDAO.class);
    private List<Route> routeList = new ArrayList<>();

    @InjectMocks
    private RouteService service = new RouteServiceImpl(serviceValidator,daoMock,graphMock);





    City city;
    City city2;
    City city3;
    City city4;

    @Before
    public void setUp(){
        graph = new DirectedMultigraph<City, Route>(Route.class);
        City first = new City("Krakow", "Poland");
        City second = new City("Budapest", "Hungary");
        graph.addVertex(first);
        graph.addVertex(second);
        route = new Route(first, second, "10", 20.0, 52, 345.5);
        route.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        graph.addEdge(first, second, route);

        city = new City("Amsterdam", "Netherland");
        city2 = new City("Köln", "Germany");
        Route route1 = new Route(city, city2, "100A", 35.0, 52, 355.0);
        route.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        city3 = new City("Basel", "Switzerland");
        city4 = new City("Zürich", "Switzerland");
        Route route2 = new Route(city3, city4, "100A", 80.0, 52, 150.0);
        route.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        routeList.add(route1);
        routeList.add(route2);

    }

    @After
    public void tearDown(){
        routeList.clear();
    }


    @Test
    public void testGraphDoesNotContainStart() throws PersistenceException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        when(daoMock.initializeGraph()).thenReturn(routeList);
        service.initializeGraph();
        Assert.assertTrue(graph.containsVertex(city));
    }

    @Test
    public void testGraphDoesNotContainEnd() throws PersistenceException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        when(daoMock.initializeGraph()).thenReturn(routeList);
        service.initializeGraph();
        Assert.assertTrue(graph.containsVertex(city2));
    }

    @Test
    public void testGraphDoesNotContainSecondStart() throws PersistenceException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        when(daoMock.initializeGraph()).thenReturn(routeList);
        service.initializeGraph();
        Assert.assertTrue(graph.containsVertex(city3));
    }

    @Test
    public void testGraphDoesNotContainSecondEnd() throws PersistenceException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        when(daoMock.initializeGraph()).thenReturn(routeList);
        service.initializeGraph();
        Assert.assertTrue(graph.containsVertex(city4));
    }

    @Test
    public void testGraphContainsStart() throws PersistenceException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        when(daoMock.initializeGraph()).thenReturn(routeList);
        service.initializeGraph();
        Assert.assertFalse(graph.addVertex(new City("Krakow", "Poland")));
    }

    @Test
    public void testGraphContainsEnd() throws PersistenceException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        when(daoMock.initializeGraph()).thenReturn(routeList);
        service.initializeGraph();
        Assert.assertFalse(graph.addVertex(new City("Budapest", "Hungary")));
    }

    @Test
    public void testGraphDoesNotContainRoute() throws PersistenceException, ServiceException {
        when(graphMock.getGraph()).thenReturn(graph);
        when(daoMock.initializeGraph()).thenReturn(routeList);
        Assert.assertFalse(graph.containsEdge(routeList.get(0)));
        service.initializeGraph();
        Assert.assertTrue(graph.containsEdge(routeList.get(0)));
    }
}
