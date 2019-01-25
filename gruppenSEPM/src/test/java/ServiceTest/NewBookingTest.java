package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.BookingServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.RouteServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.validator.ServiceValidator;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewBookingTest {
    private ServiceValidator validatorMock = mock(ServiceValidator.class);
    private RouteDAO routeDaoMock = mock(RouteDAO.class);
    private BookingDAO bookingDaoMock = mock(BookingDAO.class);
    private Graph graphMock = mock(Graph.class);

    private  DirectedMultigraph<City, Route> graph;

    private List<Route> routeList = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private List<String> buses = new ArrayList<>();


    private City krakow;
    private City budapest;
    private City amesterdam;
    private City koeln;
    private City basel;
    private City zuerich;
    private City vienna;
    @InjectMocks
    private BookingService bookingService = new BookingServiceImpl(bookingDaoMock,routeDaoMock,graphMock);
    @InjectMocks
    private RouteService routeService = new RouteServiceImpl(validatorMock,routeDaoMock,graphMock);



    @Before
    public void setUp() throws PersistenceException {

        graph = new DirectedMultigraph<City, Route>(Route.class);
        krakow = new City("krakow", "Poland"); krakow.setBlindBooking(true);
        budapest = new City("budapest", "Hungary"); budapest.setBlindBooking(true);
        amesterdam = new City("Amsterdam", "Netherland"); amesterdam.setBlindBooking(true);
        basel = new City("Basel", "Switzerland");
        zuerich = new City("Zürich", "Switzerland");
        koeln = new City("Köln", "Germany");
        vienna = new City("Vienna", "Austria");
        graph.addVertex(krakow);
        graph.addVertex(budapest);
        graph.addVertex(amesterdam);
        graph.addVertex(basel);
        graph.addVertex(zuerich);
        graph.addVertex(koeln);
        graph.addVertex(vienna);
        cities.add(krakow);cities.add(budapest);cities.add(amesterdam);cities.add(basel);
        cities.add(zuerich);cities.add(koeln);cities.add(vienna);

        Route r = new Route(krakow, budapest, "10", 20.0, 52, 345.5);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4),LocalDateTime.now().plusDays(4).plusHours(2)));
        r.setRid(1L);
        graph.addEdge(krakow, budapest, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(krakow, amesterdam, "10", 20.0, 52, 345.5);
        r.setRid(2L);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4).plusMinutes(10),LocalDateTime.now().plusDays(4).plusHours(2)));
        graph.addEdge(krakow, amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(budapest, amesterdam, "10", 20.0, 52, 345.5);
        r.setRid(3L);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4).plusHours(5),LocalDateTime.now().plusDays(4).plusHours(7)));
        graph.addEdge(budapest,amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(budapest, amesterdam, "10", 20.0, 52, 345.5);
        r.setRid(4L);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4).plusHours(1),LocalDateTime.now().plusDays(4).plusHours(5)));
        graph.addEdge(budapest,amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        //return
        r = new Route(amesterdam, krakow, "10", 20.0, 52, 345.5);
        r.setRid(2L);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4).plusMinutes(10).plusDays(1),LocalDateTime.now().plusDays(4).plusHours(2).plusDays(1)));
        graph.addEdge(amesterdam, krakow, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        //sorting
        r = new Route(basel, zuerich, "10", 30.0, 52, 300.0);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(6),LocalDateTime.now().plusDays(6).plusHours(1)));
        r.setRid(1L);
        graph.addEdge(basel, zuerich, r);
        routeList.add(r);
        //sorting
        r = new Route(basel, zuerich, "10", 50.0, 52, 200.0);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(5),LocalDateTime.now().plusDays(5).plusHours(2)));
        r.setRid(1L);
        graph.addEdge(basel, zuerich, r);
        routeList.add(r);
        r = new Route(basel, zuerich, "10", 40.0, 52, 100.0);
        r.setTimetable(new Timetable(LocalDateTime.now().plusDays(4),LocalDateTime.now().plusDays(4).plusHours(3)));
        r.setRid(1L);
        graph.addEdge(basel, zuerich, r);
        routeList.add(r);

        when(graphMock.getGraph()).thenReturn(graph);
        when(routeDaoMock.initializeGraph()).thenReturn(routeList);
        when(routeDaoMock.getAllCities()).thenReturn(cities);
        when(routeDaoMock.getAllBuses()).thenReturn(buses);
        List<String> names = new ArrayList<>();
        for(City city :cities){
            names.add(city.getName());
        }
        when(routeDaoMock.getAllCitiesNames()).thenReturn(names);

    }

    @Test
    public void searchRoutesTest() throws PersistenceException, ServiceException {


        //route with transfers
        Route search = new Route();
        search.setStartDestination(krakow);
        search.setEndDestination(amesterdam);
        search.setDepartureDate(LocalDate.now());
        search.setWithTransfer(true);
        List<List<Route>> routes= routeService.searchRoutes(search,false,"default",true);
        Assert.assertEquals(2,routes.size());
        //check if route 3 is in List. (It shouldn't be)
        for(List<Route> list :routes){
            Assert.assertFalse(list.contains(routeList.get(3)));
        }

        //direct route
        search.setStartDestination(krakow);
        search.setEndDestination(amesterdam);
        search.setWithTransfer(false);
        routes= routeService.searchRoutes(search,false,"default",true);
        Assert.assertEquals(1,routes.size());

        //not existing route
        search.setStartDestination(basel);
        search.setEndDestination(amesterdam);
        search.setWithTransfer(false);
        routes= routeService.searchRoutes(search,false,"default",true);
        Assert.assertEquals(0,routes.size());

        // return route
        search.setStartDestination(krakow);
        search.setEndDestination(amesterdam);
        search.setWithTransfer(false);
        search.setReturnDate(LocalDate.now());
        search.setIncludeReturn(true);
        routes= routeService.searchRoutes(search,true,"default",true);
        Assert.assertEquals(1,routes.size());

    }

    @Test(expected = ServiceException.class)
    public void isReturnValidTest() throws ServiceException {
        Assert.assertTrue(bookingService.isReturnValid(routeList.get(0),routeList.get(2)));
        Assert.assertFalse(bookingService.isReturnValid(routeList.get(0),routeList.get(3)));
        bookingService.isReturnValid(null,routeList.get(2));
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void loadBookingEntriesTest() throws ServiceException, PersistenceException {
        when(bookingDaoMock.loadBookingEntries()).thenReturn(new ArrayList<>());
        Assert.assertNotNull(bookingService.loadBookingEntries());
        Assert.assertTrue(bookingService.loadBookingEntries().isEmpty());
        when(bookingDaoMock.loadBookingEntries()).thenThrow(PersistenceException.class);
        bookingService.loadBookingEntries();
        Assert.fail();
    }

    @Test(expected = ServiceException.class)
    public void loadBookingsTest() throws ServiceException, PersistenceException {
        when(bookingDaoMock.loadBookings()).thenReturn(new ArrayList<>());
        Assert.assertNotNull(bookingService.loadBookings());
        Assert.assertTrue(bookingService.loadBookings().isEmpty());
        when(bookingDaoMock.loadBookings()).thenThrow(PersistenceException.class);
        bookingService.loadBookings();
        Assert.fail();
    }

  /*  @Test(expected = ServiceException.class)
    public void createBookingTest() throws ServiceException, PersistenceException {
        doThrow(new PersistenceException()).when(bookingDaoMock).createBooking(any(Booking.class));
        bookingService.createBooking(any(Booking.class));
    }*/

    /*@Test(expected = ServiceException.class)
    public void calculatePriceTest() throws ServiceException, PersistenceException {
        Booking booking = new Booking(1L,Booking.Status.COMPLETED,0.0);
        List<BookingEntry> entries = new ArrayList<>();
        Customer c = new Customer("a","b","email");
        entries.add(new BookingEntry(c,1,routeList.get(0)));
        entries.add(new BookingEntry(c,1,routeList.get(2)));
        booking.setEntries(entries);

        Assert.assertEquals(40.0,bookingService.calculatePrice(booking),0);

        booking.setEntries(new ArrayList<>());
        bookingService.calculatePrice(booking);

    }*/

    @Test
    public void sortTest() throws ServiceException, PersistenceException {
        Route search = new Route();
        search.setStartDestination(basel);
        search.setEndDestination(zuerich);
        search.setDepartureDate(LocalDate.now());
        List<List<Route>> routes = routeService.searchRoutes(search,false, "cheapest",true);

        Assert.assertTrue(routesEqual(routes.get(0).get(0),routeList.get(5)));
        Assert.assertTrue(routesEqual(routes.get(1).get(0),routeList.get(7)));
        Assert.assertTrue(routesEqual(routes.get(2).get(0),routeList.get(6)));

        routes = routeService.searchRoutes(search,false, "fastest",true);
        Assert.assertTrue(routesEqual(routes.get(0).get(0),routeList.get(5)));
        Assert.assertTrue(routesEqual(routes.get(1).get(0),routeList.get(6)));
        Assert.assertTrue(routesEqual(routes.get(2).get(0),routeList.get(7)));

        routes = routeService.searchRoutes(search,false, "shortest",true);
        Assert.assertTrue(routesEqual(routes.get(0).get(0),routeList.get(7)));
        Assert.assertTrue(routesEqual(routes.get(1).get(0),routeList.get(6)));
        Assert.assertTrue(routesEqual(routes.get(2).get(0),routeList.get(5)));

        routes = routeService.searchRoutes(search,false, "default",true);
        Assert.assertTrue(routesEqual(routes.get(0).get(0),routeList.get(7)));
        Assert.assertTrue(routesEqual(routes.get(1).get(0),routeList.get(6)));
        Assert.assertTrue(routesEqual(routes.get(2).get(0),routeList.get(5)));

    }

    private boolean routesEqual(Route r1 ,Route r2){
        return  r1.getStartDestination().equals(r2.getStartDestination())
            && r1.getEndDestination().equals(r2.getEndDestination()) &&
            r1.getTimetable().equals(r2.getTimetable());
    }

    @Test
    public void getCitiesFromBlindBooking_shouldNotBeEmpty() throws PersistenceException {
        vienna.setBlindBooking(true);
        List<City> currentCities = routeDaoMock.getAllCities();
        for (City c:
            currentCities) {
            if (c.getName().equalsIgnoreCase("vienna")){
                Assert.assertTrue(c.isBlindBooking());
                break;
            }
        }
    }

    @Test (expected = ServiceException.class)
    public void modifyCityThatIsNotFromBlindBooking_shouldFail() throws ServiceException {
        bookingService.validateEntryForBlindBookingModification(Collections.singletonList("Berlin"));
    }


    @Test
    public void modifyCityFromBlindBooking_shouldNotThrowException() throws ServiceException {
        bookingService.validateEntryForBlindBookingModification(Collections.singletonList("Vienna"));
    }


    @Test
    public void getAllBuses_shouldNotBeEmpty() throws PersistenceException {
        List<String> buses = routeDaoMock.getAllBuses();
        Assert.assertTrue(!buses.isEmpty());
    }

}
