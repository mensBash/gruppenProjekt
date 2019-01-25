package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Booking;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.BookingEntry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.BookingServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CancelTheTripServiceTest {

    private BookingDAO daoMock = mock(BookingDAO.class);
    private Graph graphMock = mock(Graph.class);
    private RouteDAO routeDAO = mock(RouteDAO.class);
    private Booking booking;
    private Route route;
    private BookingEntry bookingEntry;
    private List<BookingEntry> entries;


    @InjectMocks
    private BookingService service = new BookingServiceImpl(daoMock, routeDAO, graphMock);

    @Before
    public void setUp() {
        booking = new Booking();
        booking.setPrice(100.0);
        route = new Route();
        bookingEntry = new BookingEntry();
        route.setTimetable(new Timetable(null, LocalDateTime.of(2018,10,10,0,0,0)));
        entries = new ArrayList<>();
    }

    @Test
    public void canceling1MonthBeforeDepartureTime() throws ServiceException, PersistenceException {
        route.getTimetable().setDepartureTime(LocalDateTime.now().plusMonths(1));
        bookingEntry.setRoute(route);
        entries.add(bookingEntry);
        booking.setEntries(entries);
        service.cancel(booking);
        Mockito.verify(daoMock).deleteBooking(booking);
    }

    @Test
    public void cancelingBefore25HoursFromDepartureTime() throws ServiceException, PersistenceException {
        route.getTimetable().setDepartureTime(LocalDateTime.now().plusHours(25));
        bookingEntry.setRoute(route);
        entries.add(bookingEntry);
        booking.setEntries(entries);
        service.cancel(booking);
        Mockito.verify(daoMock).deleteBooking(booking);
    }

    @Test(expected = ServiceException.class)
    public void cancelingAfter24HourFromDepartureTime() throws ServiceException {
        route.getTimetable().setDepartureTime(LocalDateTime.now().plusHours(20));
        bookingEntry.setRoute(route);
        entries.add(bookingEntry);
        booking.setEntries(entries);
        service.cancel(booking);
    }

    @Test(expected = ServiceException.class)
    public void cancelingWithAnEmptyDepartureTime() throws ServiceException {
        bookingEntry.setRoute(route);
        entries.add(bookingEntry);
        booking.setEntries(entries);
        service.cancel(booking);
    }

}
