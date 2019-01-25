package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.RouteServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.validator.ServiceValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SeatBookingTest {

    private RouteDAO daoMock = mock(RouteDAO.class);

    private ServiceValidator serviceValidator = mock(ServiceValidator.class);
    private Graph graphMock = mock(Graph.class);


    @InjectMocks
    private RouteService service = new RouteServiceImpl(serviceValidator,daoMock,graphMock);

    @Test(expected = ServiceException.class)
    public void loadingSeatsRouteNull() throws ServiceException {
        service.loadSeats(null);

    }
    @Test(expected = ServiceException.class)
    public void loadingSeatsRouteEmpty() throws ServiceException {
        Route route = new Route();
        service.loadSeats(route);

    }
    @Test
    public void loadingSeatsValidInput() throws ServiceException, PersistenceException {
        Route route = new Route();
        route.setRid(1L);
        List<Integer> seatsList = new LinkedList();
        when(daoMock.loadSeats(route)).thenReturn(seatsList);
        Assert.assertEquals(service.loadSeats(route), seatsList);
        Mockito.verify(daoMock).loadSeats(route);
    }
}
