package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.BookingServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.RouteServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.exception.NotFoundException;
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
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FareFinderTest{
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

        vienna = new City("Vienna", "Austria");
        graph.addVertex(krakow);
        graph.addVertex(budapest);
        graph.addVertex(amesterdam);

        graph.addVertex(vienna);
        cities.add(krakow);cities.add(budapest);cities.add(amesterdam);cities.add(vienna);
        LocalDateTime baseDate = LocalDateTime.of(2018,6,23,2,0);
        Route r = new Route(krakow, budapest, "10", 20.0, 52, 345.5);
        r.setTimetable(new Timetable(baseDate.plusDays(4),baseDate.plusDays(4).plusHours(2)));
        r.setRid(1L);
        graph.addEdge(krakow, budapest, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(krakow, amesterdam, "10", 20.0, 52, 345.5);
        r.setRid(2L);
        r.setTimetable(new Timetable(baseDate.plusDays(4).plusMinutes(10),baseDate.plusDays(4).plusHours(2)));
        graph.addEdge(krakow, amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(krakow, vienna, "10", 20.0, 52, 345.5);
        r.setRid(2L);
        r.setTimetable(new Timetable(baseDate.plusMonths(2).plusDays(5).plusMinutes(10),baseDate.plusMonths(2).plusDays(5).plusHours(2)));
        graph.addEdge(krakow, vienna, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(budapest, amesterdam, "10", 20.0, 52, 345.5);
        r.setRid(3L);
        r.setTimetable(new Timetable(baseDate.plusDays(4).plusHours(5),baseDate.plusDays(4).plusHours(7)));
        graph.addEdge(budapest,amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(budapest, amesterdam, "10", 25.0, 52, 345.5);
        r.setRid(4L);
        r.setTimetable(new Timetable(baseDate.plusDays(5).plusHours(1),baseDate.plusDays(5).plusHours(5)));
        graph.addEdge(budapest,amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());
        r = new Route(budapest, amesterdam, "10", 5.0, 52, 345.5);
        r.setRid(3L);
        r.setTimetable(new Timetable(baseDate.plusDays(6).plusHours(5),baseDate.plusDays(6).plusHours(7)));
        graph.addEdge(budapest,amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());

        r = new Route(budapest, amesterdam, "10", 10.0, 52, 345.5);
        r.setRid(4L);
        r.setTimetable(new Timetable(baseDate.plusMonths(1).plusDays(4).plusHours(1),baseDate.plusMonths(1).plusDays(4).plusHours(5)));
        graph.addEdge(budapest,amesterdam, r);
        routeList.add(r);
        buses.add(r.getBusNumber());



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

    @Test(expected = NotFoundException.class)
    public void findFareDepartureTimeTest() throws NotFoundException, ServiceException {
      Map<FareItem,Route> map= routeService.findFares(krakow,vienna,null);
      Assert.assertEquals(1,map.size());
      map= routeService.findFares(krakow,vienna,LocalDate.now().plusMonths(3));
    }

    @Test(expected = NotFoundException.class)
    public void findFareEndDestinationTest() throws NotFoundException, ServiceException {
        Map<FareItem,Route> map= routeService.findFares(krakow,null,null);
        Assert.assertEquals(3,map.size());
        map= routeService.findFares(krakow,null,LocalDate.now().plusMonths(2));
        Assert.assertEquals(1,map.size());
        map= routeService.findFares(krakow,null,LocalDate.now().plusMonths(4));
    }

    @Test
    public void getRoutesForMonthTest() throws  ServiceException {
        Route r = new Route(budapest,amesterdam,"",0.0,0,0.0);
        r.setDepartureDate(LocalDate.now());
        r.setWithTransfer(true);
        Map<LocalDate, Route> map = routeService.getRoutesForMonth(r);
        Assert.assertEquals(30,map.size());
        List<Route> list = new ArrayList<>(map.values());
        Assert.assertEquals(50,routeService.calculatePrice(list),0);
        r.setDepartureDate(LocalDate.now().plusMonths(1));
        map = routeService.getRoutesForMonth(r);
        list.clear();
        list.addAll(map.values());
        Assert.assertEquals(31,map.size());
        Assert.assertEquals(10,routeService.calculatePrice(list),0);
        r.setDepartureDate(LocalDate.now().plusMonths(2));
        map = routeService.getRoutesForMonth(r);
        list.clear();
        list.addAll(map.values());
        Assert.assertEquals(31,map.size());
        Assert.assertEquals(0,routeService.calculatePrice(list),0);
    }
}
