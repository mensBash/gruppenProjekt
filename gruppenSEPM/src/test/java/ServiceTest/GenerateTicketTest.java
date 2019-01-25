package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.GeneratePDFService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.SimpleGeneratePDFService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GenerateTicketTest {

    @InjectMocks
    private GeneratePDFService service = new SimpleGeneratePDFService();

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    private List<Booking> bookings;

    @Before
    public void init() {
        bookings = new LinkedList<>();

        bookings.add(new Booking(1L, Booking.Status.COMPLETED, 0.0d));
        bookings.add(new Booking(2L, Booking.Status.COMPLETED, 0.0d));
        bookings.add(new Booking(3L, Booking.Status.COMPLETED, 0.0d));

        List<City> cities = new LinkedList<>();
        List<Route> routes = new LinkedList<>();

        cities.add(new City("Vienna", "Austria"));
        cities.add(new City("Berlin", "Germany"));

        routes.add(new Route(cities.get(0), cities.get(1), "a991", 54.5, 51, 681.1));
        routes.add(new Route(cities.get(1), cities.get(0), "b991", 69.2, 51, 681.1));

        Customer max = new Customer();
        max.setFirstName("Max");
        max.setLastName("MusterMann");

        Customer lisa = new Customer();
        lisa.setFirstName("Lisa");
        lisa.setLastName("MusterFrau");

        List<BookingEntry> bookingEntries = new LinkedList<>();

        BookingEntry bE = new BookingEntry(max, 9, ((LinkedList<Route>) routes).getFirst());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now(), LocalDateTime.now().plusHours(4)));

        bookingEntries.add(bE);

        bE = new BookingEntry(lisa, 7, ((LinkedList<Route>) routes).getFirst());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now(), LocalDateTime.now().plusHours(4)));

        bookingEntries.add(bE);

        bookings.get(0).setEntries(bookingEntries);

        bookingEntries = new LinkedList<>();

        bE = new BookingEntry(max, 9, ((LinkedList<Route>) routes).getLast());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now(), LocalDateTime.now().plusHours(8)));

        bookingEntries.add(bE);

        bE = new BookingEntry(lisa, 7, ((LinkedList<Route>) routes).getLast());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now(), LocalDateTime.now().plusHours(8)));

        bookingEntries.add(bE);

        bookings.get(1).setEntries(bookingEntries);

        bookingEntries = new LinkedList<>();

        bE = new BookingEntry(max, 9, ((LinkedList<Route>) routes).getFirst());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now(), LocalDateTime.now().plusHours(8)));

        bookingEntries.add(bE);

        bE = new BookingEntry(max, 7, ((LinkedList<Route>) routes).getLast());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(8)));

        bookingEntries.add(bE);

        bE = new BookingEntry(lisa, 9, ((LinkedList<Route>) routes).getFirst());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now(), LocalDateTime.now().plusHours(8)));

        bookingEntries.add(bE);

        bE = new BookingEntry(lisa, 7, ((LinkedList<Route>) routes).getLast());
        bE.getRoute().setTimetable(new Timetable(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(8)));

        bookingEntries.add(bE);

        bookings.get(2).setEntries(bookingEntries);

        long i = 0L;
        for (Booking b :
            bookings) {
            b.setBookingNr(++i);
        }


    }


    @Test
    public void manuelCheck() throws ServiceException {

        /* TODOx: this test is a manuel test, u should check the info manually,
           please be sure to see all of listed following info in the generated pdf,
           the pdf names are Max_MusterMann_Hashcode.pdf x 3 and Lisa_MusterFrau_Hashcode.pdf x 3,
           check the stdout to know the Directory path.
         */
        //System.out.println(Paths.get(System.getProperty("user.home") + "/busFirmaTickets"));

       /* for the first method downstairs.

        QR code include all of the down listed Info
             booking number: #nummer
         First name: Max     Last name: MusterMann
                Outward:
                Start destination: Vienna, Austria
                Bus number: a991
                Departure: date time
                Arrival: date time
                End destination: Berlin, Germany
                Total Price: 54.5 EUR

       --------------------------------------------------------------------------
        QR code include all of the down listed Info
             booking number: #nummer
         First name: Lisa    Last name: MusterFrau
            Outward:
            Start destination: Vienna, Austria
            Bus number: a991
            Departure: date time
            Arrival: date time
            End destination: Berlin, Germany
            Total Price: 54.5 EUR
        */
        service.generatePDF(bookings.get(0));

         /* for the second method downstairs.

        QR code include all of the down listed Info
             booking number: #nummer
         First name: Max     Last name: MusterMann
            Outward:
            Start destination: Berlin, Germany
            Bus number: b991
            Departure: date time
            Arrival: date time
            End destination: Vienna, Austria
            Total Price: 69.2 EUR

       --------------------------------------------------------------------------
        QR code include all of the down listed Info
             booking number: #nummer
         First name: Lisa    Last name: MusterFrau
            Outward:
            Start destination: Berlin, Germany
            Bus number: b991
            Departure: date time
            Arrival: date time
            End destination: Vienna, Austria
            Total Price: 69.2 EUR
        */
        service.generatePDF(bookings.get(1));

        /* for the last method downstairs.

        QR code include all of the down listed Info
             booking number: #nummer
         First name: Max     Last name: MusterMann
            Outward:
            Start destination: Vienna, Austria
            Bus number: a991
            Departure: 2018-06-19 15:35
            Arrival: 2018-06-19 23:35
            End destination: Berlin, Germany
            Return:
            Start destination: Berlin, Germany
            Bus number: b991
            Departure: date time
            Arrival: date time
            End destination: Vienna, Austria
            Total Price: 123.7 EUR
       --------------------------------------------------------------------------
        QR code include all of the down listed Info
             booking number: #nummer
         First name: Lisa    Last name: MusterFrau
            Outward:
            Start destination: Vienna, Austria
            Bus number: a991
            Departure: date time
            Arrival: date time
            End destination: Berlin, Germany
            Return:
            Start destination: Berlin, Germany
            Bus number: b991
            Departure: date time
            Arrival: date time
            End destination: Vienna, Austria
            Total Price: 123.7 EUR
        */
        service.generatePDF(bookings.get(2));
    }

}
