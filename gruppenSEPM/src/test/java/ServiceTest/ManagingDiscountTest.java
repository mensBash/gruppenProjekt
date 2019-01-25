package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.BookingServiceImpl;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class ManagingDiscountTest {


    private static Booking booking;
    private BookingDAO bookingDAOMock = mock(BookingDAO.class);
    private RouteDAO routeDAOMock = mock(RouteDAO.class);
    private Graph graphMock = mock(Graph.class);
    @InjectMocks
    private BookingService service = new BookingServiceImpl(bookingDAOMock, routeDAOMock, graphMock);


    @BeforeClass
    public static void init() {
        booking = new Booking(1994L, Booking.Status.COMPLETED, 40.0);
    }

    @Test
    public void checkDiscount() throws ServiceException {

        // mixed customers one is premium and one is not

        Customer maxPremium = new Customer("max", "mustermann",
            "1990", "mustermann@mail.com", LocalDateTime.now().minusYears(28).toLocalDate());

        Customer max = new Customer("max", "mustermann", null, "mustermann@mail.com", LocalDateTime.now().minusYears(28).toLocalDate());

        Route route = new Route(new City("Vienna", "Austria"), new City("Berlin", "Germany"),
            "110A", 40.0, 50, 680.0);

        List<BookingEntry> bookingEntry = new LinkedList<>();

        bookingEntry.add(new BookingEntry(maxPremium, 9, route));
        bookingEntry.add(new BookingEntry(max, 9, route));


        booking.setEntries(bookingEntry);
        booking.setPrice(route.getPrice() * 2);

        double price = booking.getPrice();
        service.calculatePrice(booking);
        if (booking.getPrice() == price) {
            throw new ServiceException("discount does not work");
        }
    }


    @Test
    public void checkDiscountWithNoPremiumCustomers() throws ServiceException {
        // two customers, who are not premium.

        Customer max = new Customer("max", "mustermann", null, "mustermann@mail.com", LocalDateTime.now().minusYears(28).toLocalDate());

        Route route = new Route(new City("Vienna", "Austria"), new City("Berlin", "Germany"),
            "110A", 40.0, 50, 680.0);

        List<BookingEntry> bookingEntry = new LinkedList<>();

        bookingEntry.add(new BookingEntry(max, 9, route));
        bookingEntry.add(new BookingEntry(max, 9, route));


        booking.setEntries(bookingEntry);
        booking.setPrice(route.getPrice() * 2);

        double price = booking.getPrice();
        service.calculatePrice(booking);
        if (booking.getPrice() != price) {
            throw new ServiceException("discount works with not premium customer");
        }
    }

    @Test
    public void checkDiscountForTwoPremiumCustomers() throws ServiceException {
        // two customers, who are premium.

        Customer maxPremium = new Customer("max", "mustermann",
            "1990", "mustermann@mail.com", LocalDateTime.now().minusYears(28).toLocalDate());

        Route route = new Route(new City("Vienna", "Austria"), new City("Berlin", "Germany"),
            "110A", 40.0, 50, 680.0);

        List<BookingEntry> bookingEntry = new LinkedList<>();

        bookingEntry.add(new BookingEntry(maxPremium, 9, route));
        bookingEntry.add(new BookingEntry(maxPremium, 9, route));


        booking.setEntries(bookingEntry);
        booking.setPrice(route.getPrice() * 2);

        double price = booking.getPrice();
        service.calculatePrice(booking);
        if (booking.getPrice() != price - price * 0.1) {
            throw new ServiceException("discount does not work");
        }
    }

}
