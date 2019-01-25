package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.RouteServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidRouteException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.validator.ServiceValidator;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModifyRoutesTest {

    private ServiceValidator serviceValidator = mock(ServiceValidator.class);
    private static Graph graphMock = mock(Graph.class);
    private RouteDAO daoMock = mock(RouteDAO.class);
    @InjectMocks
    private RouteService service = new RouteServiceImpl(serviceValidator,daoMock,graphMock);

    private static DirectedMultigraph<City, Route> graph = graphMock.getGraph();

    @Test (expected = ServiceException.class)
    public void updateEmptyRouteServiceException() throws InvalidRouteException, ServiceException, PersistenceException {
        doThrow(PersistenceException.class).when(daoMock).updateRoute(null);
        service.updateRoute(null);
    }

    @Test (expected = ServiceException.class)
    public void updateEmptyTimetableServiceException() throws ServiceException, PersistenceException {
        doThrow(PersistenceException.class).when(daoMock).updateTimetable(null, null);
        service.updateTimetable(null, null);
    }

    @Test
    public void updateExistingRouteValid() throws InvalidRouteException, ServiceException, PersistenceException {
        Route route1
            = new Route(new City("Krakow", "Poland"),new City("Budapest", "Hungary"), "11", 20.0, 50, 340.0);
        doNothing().when(graphMock).createNewGraph();
        service.updateRoute(route1);
    }
    @Test
    public void updateExistingTimetableValid() throws InvalidRouteException, ServiceException, PersistenceException {
        Route route1 = new Route(new City("Krakow", "Poland"),new City("Budapest", "Hungary"), "11", 20.0, 50, 340.0);
        Map<Timetable, Double> timetableMap = new HashMap<>();
        timetableMap.put(new Timetable(LocalDateTime.of(2018,6,6,22,22,0,0), LocalDateTime.of(2018,6,7,2,22,0,0)),25.0);
        doNothing().when(graphMock).createNewGraph();
        service.updateTimetable(route1, timetableMap);
    }

}
