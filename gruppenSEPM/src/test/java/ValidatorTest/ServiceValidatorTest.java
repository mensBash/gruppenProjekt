package ValidatorTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.BookingServiceImpl;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.*;

import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ServiceValidatorTest {

    private RouteDAO daoMock = mock(RouteDAO.class);

    private ServiceValidator serviceValidator = new ServiceValidator();

    private Graph graph = mock(Graph.class);

    @InjectMocks
    private RouteService service = new RouteServiceImpl(serviceValidator,daoMock,graph);

    private DirectedMultigraph<City, Route> graphInstance = new DirectedMultigraph<City, Route>(Route.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private List<City> cities = new ArrayList<>();

    @Before
    public void init() {
        when(graph.getGraph()).thenReturn(graphInstance);
        cities.add(new City("Vienna", "Austria"));
        cities.add(new City("VienNa", "Austria"));
        cities.add(new City("Graz", "Austria"));
        cities.add(new City(null, "Austria"));
        cities.add(new City(null, null));
        cities.add(new City("Berlin", "Germany"));
        cities.add(new City("Munich", "Germany"));
    }

    @Test
    public void addingEmptyRoute() throws InvalidRouteException, ServiceException {
        thrown.expect(InvalidRouteException.class);
        thrown.reportMissingExceptionWithMessage("InvalidRouteException expected");
        serviceValidator.validateServiceRoute(new Route());
    }

    @Test
    public void addingValidTimetable() throws ServiceValidatorException {
        LocalDateTime departure = LocalDateTime.now();
        LocalDateTime arrival = LocalDateTime.now().plusHours(3);
        List<Timetable> timetableList = new ArrayList<>();
        Timetable timetable1 = new Timetable(departure, arrival);
        Timetable timetable2 = new Timetable(departure.plusDays(2), arrival.plusDays(2));
        timetableList.add(timetable1);
        timetableList.add(timetable2);
        Timetable timetoAdd = new Timetable(departure.plusDays(2).plusHours(4), arrival.plusDays(2).plusHours(5));
        serviceValidator.checkOverlap(timetoAdd, timetableList);
    }

    @Test (expected = ServiceValidatorException.class)
    public void overlapingTimetableInvalid() throws ServiceValidatorException {
        LocalDateTime departure = LocalDateTime.now();
        LocalDateTime arrival = LocalDateTime.now().plusHours(3);
        List<Timetable> timetableList = new ArrayList<>();
        Timetable timetable1 = new Timetable(departure, arrival);
        Timetable timetable2 = new Timetable(departure.plusDays(2), arrival.plusDays(2));
        timetableList.add(timetable1);
        timetableList.add(timetable2);
        Timetable timetoAdd = new Timetable(departure.plusDays(2).plusHours(1), arrival.plusDays(2).plusHours(2));
        serviceValidator.checkOverlap(timetoAdd, timetableList);
    }

    @Test
    public void addingInvalidRouteDestination() throws InvalidRouteException, ServiceException {

        thrown.expect(InvalidRouteException.class);
        thrown.expectMessage("Invalid start destination");
        thrown.expectMessage("Invalid end destination");
        thrown.reportMissingExceptionWithMessage("InvalidRouteException expected");
        serviceValidator.validateServiceRoute(new Route(null, null, "123", 25.5, 22, 22.4));


    }
    @Test
    public void addingInvalidRouteNumbers() throws InvalidRouteException, ServiceException {

        thrown.expect(InvalidRouteException.class);
        thrown.expectMessage("Invalid price");
        thrown.expectMessage("Invalid distance");
        thrown.expectMessage("Invalid capacity");

        thrown.reportMissingExceptionWithMessage("InvalidRouteException expected");
        serviceValidator.validateServiceRoute(new Route(cities.get(0), cities.get(2), "123", -5.3, -5, -25.5));

    }

    @Test
    public void addingSameStartEndDest() throws InvalidRouteException, ServiceException {

        thrown.expect(InvalidRouteException.class);
        thrown.reportMissingExceptionWithMessage("InvalidRouteException expected");
        serviceValidator.validateServiceRoute(new Route(cities.get(0), cities.get(1), "123", 5.3, 5, 25.5));


    }
    @Test
    public void addingInvalidRouteTimetable() throws InvalidRouteException, ServiceException {

            thrown.expect(InvalidRouteException.class);
            thrown.reportMissingExceptionWithMessage("InvalidRouteException expected");
            thrown.expectMessage("Invalid departure date");
            thrown.expectMessage("Invalid arrival date");
            Route r= new Route(cities.get(0), cities.get(2), "123", 5.0, 6, 25.5);
            r.setTimetable(new Timetable(null, null));
            serviceValidator.validateServiceRoute(r);

    }
    @Test
    public void addingArrivalBeforeDepart() throws InvalidRouteException, ServiceException {

            thrown.expect(InvalidRouteException.class);
            thrown.reportMissingExceptionWithMessage("InvalidRouteException expected");
            thrown.expectMessage("Arrival date must be after departure date");
            Route r = new Route(cities.get(0), cities.get(2), "123", 5.0, 6, 25.5);
            r.setTimetable(new Timetable(LocalDateTime.now(),LocalDateTime.now().minusDays(2)));
            serviceValidator.validateServiceRoute(r);
    }

    @Test
    public void throwsExceptionOnCreateRoute() throws ServiceException, InvalidRouteException, PersistenceException {

        doThrow(PersistenceException.class).when(daoMock).createRoute(any(Route.class));
        thrown.expect(ServiceException.class);
        thrown.reportMissingExceptionWithMessage("ServiceException expected");
        Route r = new Route(cities.get(0), cities.get(2), "123", 5.0, 6, 25.5);
        service.addRoute(r);

    }

    @Test
    public void throwsExceptionOnCreateCities() throws ServiceException, InvalidRouteException, PersistenceException {

        doThrow(PersistenceException.class).when(daoMock).createCities(any(Route.class),anyBoolean());
        thrown.expect(ServiceException.class);
        thrown.reportMissingExceptionWithMessage("ServiceException expected");
        Route r= new Route(cities.get(0), cities.get(2), "123", 5.0, 6, 25.5);
        service.addRoute(r);
    }


}