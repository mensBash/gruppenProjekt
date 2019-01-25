package ServiceTest;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.CustomerDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.StatisticsService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.StatisticsServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class StatisticsTest {
    private BookingDAO bookingDaoMock = mock(BookingDAO.class);
    private RouteDAO routeDAOMock = mock(RouteDAO.class);
    private CustomerDAO customerDAOMock = mock(CustomerDAO.class);
    private static Route route;
    private static Route route1;
    private LocalDate time;
    LocalDateTime from;
    private Graph graphMock = mock(Graph.class);
    private static DirectedMultigraph<City, Route> graph;
    @InjectMocks
    private StatisticsService service = new StatisticsServiceImpl(bookingDaoMock, routeDAOMock, customerDAOMock);
    @Before
    public void setUp() {
        graph = new DirectedMultigraph<City, Route>(Route.class);
        City first = new City("Vienna", "");
        City second = new City("Berlin", "");
        time = LocalDate.of(2018, 10, 10);
        from = LocalDateTime.of(2018, 10, 5,17,0,0);
        graph.addVertex(first);
        graph.addVertex(second);
        route1 = new Route(first, second, "10", 20.0, 52, 345.5);
        route1.setTimetable(new Timetable(LocalDateTime.of(2018, 7, 3, 12, 10), LocalDateTime.of(2018, 8, 4, 12, 24)));
        route1.setRid(5L);
        graph.addEdge(first, second, route1);
    }
    @BeforeClass
    public static void init(){
        route = new Route();
        route.setStartDestination(new City("Vienna", ""));
        route.setEndDestination(new City("Berlin", ""));
    }
    @Test (expected = ServiceException.class)
    public void getPricesCatchesPersistenceException() throws PersistenceException, ServiceException {
        when(bookingDaoMock.getPriceForRoute(route, time, time.plusDays(3))).thenThrow(PersistenceException.class);
        service.getPriceForRoute(route, time, time.plusDays(3));
        Mockito.verify(bookingDaoMock).getPriceForRoute(route, time, time);
    }
    @Test
    public void getPricesForBookedRoute() throws ServiceException, PersistenceException {
        when(bookingDaoMock.getPriceForRoute(route, time, time.plusDays(3))).thenReturn(100);
        int price = service.getPriceForRoute(route, time, time.plusDays(3));
        Assert.assertEquals(100, price);
        Mockito.verify(bookingDaoMock).getPriceForRoute(route, time, time.plusDays(3));
    }
    @Test (expected = ServiceException.class)
    public void getSeatsCatchesPersistenceException() throws PersistenceException, ServiceException {
        when(bookingDaoMock.getNumberOfBookedSeats(time)).thenThrow(PersistenceException.class);
        service.getNumberOfBookedSeats(time);
        Mockito.verify(bookingDaoMock).getNumberOfBookedSeats(time);
    }
    @Test
    public void getSeatsForBookedRoute() throws ServiceException, PersistenceException {
        when(bookingDaoMock.getNumberOfBookedSeats(time)).thenReturn(100);
        int seats = service.getNumberOfBookedSeats(time);
        Assert.assertEquals(100, seats);
        Mockito.verify(bookingDaoMock).getNumberOfBookedSeats(time);
    }
    @Test
    public void getValidListOfRoutes(){
        when(graphMock.getGraph()).thenReturn(graph);
        route.setRid(5L);
        Assert.assertTrue(service.getRoutes().get(0).getStartDestination().equals(route.getStartDestination())
            && service.getRoutes().get(0).getEndDestination().equals(route.getEndDestination()));
        Mockito.verify(graphMock, times(2)).getGraph();
    }
    @Test
    public void countActiveBookingsTest() throws PersistenceException, ServiceException {
        when(bookingDaoMock.getNumberAllFutureBookings()).thenReturn(1);
        int bookings = service.countActiveBookings();
        Assert.assertEquals(1, bookings);
        Mockito.verify(bookingDaoMock).getNumberAllFutureBookings();
    }
    @Test(expected = ServiceException.class)
    public void countActiveBookingsFailedTest() throws PersistenceException, ServiceException {
        when(bookingDaoMock.getNumberAllFutureBookings()).thenThrow(PersistenceException.class);
        service.countActiveBookings();
        Mockito.verify(bookingDaoMock).getNumberAllFutureBookings();
    }
    @Test
    public void countRegisteredCustomersTest() throws CustomerException, ServiceException {
        Customer customer = new Customer("Max", "Mustermann","10205aTg","example@gmail.com",LocalDate.of(1988,5,12));
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        when(customerDAOMock.loadCustomers()).thenReturn(customers);
        int numberOfCustomers = service.countRegisteredCustomers();
        Assert.assertEquals(1, numberOfCustomers);
        Mockito.verify(customerDAOMock).loadCustomers();
    }
    @Test(expected = ServiceException.class)
    public void countRegisteredCustomersFailed() throws CustomerException, ServiceException {
        when(customerDAOMock.loadCustomers()).thenThrow(CustomerException.class);
        service.countRegisteredCustomers();
        Mockito.verify(customerDAOMock).loadCustomers();
    }
    @Test
    public void calculateCapacityUtilizationTest() throws PersistenceException, ServiceException{
        when(routeDAOMock.getNumberAllFutureRoutes()).thenReturn(5);
        when(bookingDaoMock.getNumberAllFutureBookingEntries()).thenReturn(80);
        double percent = service.calculateCapacityUtilization();
        Assert.assertEquals(30.2, percent,0);
        Mockito.verify(routeDAOMock).getNumberAllFutureRoutes();
        Mockito.verify(bookingDaoMock).getNumberAllFutureBookingEntries();
    }
    @Test(expected = ServiceException.class)
    public void calculateCapacityUtilizationFailed() throws PersistenceException, ServiceException {
        when(routeDAOMock.getNumberAllFutureRoutes()).thenThrow(PersistenceException.class);
        service.calculateCapacityUtilization();
        Mockito.verify(routeDAOMock).getNumberAllFutureRoutes();
        Mockito.verify(bookingDaoMock).getNumberAllFutureBookingEntries();
    }
    @Test
    public void getNumberOfTicketsPerRouteTest() throws PersistenceException, ServiceException {
        List<TicketCounter> routes = new ArrayList<>();
        TicketCounter ticketCounter = new TicketCounter("Vienna", "Berlin", 30,20);
        routes.add(ticketCounter);
        when(routeDAOMock.getNumberOfTicketsPerRoute(from, from.plusMonths(2))).thenReturn(routes);
        List<TicketCounter> routesInService = service.getNumberOfTicketsPerRoute(from,from.plusMonths(2));
        Assert.assertEquals("Vienna",routes.get(0).getDepartureCity());
        Assert.assertEquals("Berlin", routes.get(0).getArrivalCity());
        Assert.assertEquals(30, routes.get(0).getOneWayTicketsCounter());
        Assert.assertEquals(20, routes.get(0).getReturnTicketsCounter());
        Mockito.verify(routeDAOMock).getNumberOfTicketsPerRoute(from, from.plusMonths(2));
    }
    @Test(expected = ServiceException.class)
    public void getNumberOfTicketsPerRouteFailed() throws PersistenceException, ServiceException{
        when(routeDAOMock.getNumberOfTicketsPerRoute(from, from.plusMonths(2))).thenThrow(PersistenceException.class);
        service.getNumberOfTicketsPerRoute(from, from.plusMonths(2));
        Mockito.verify(routeDAOMock).getNumberOfTicketsPerRoute(from, from.plusMonths(2));
    }
}