package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl;

import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private BookingDAO bookingDAO;

    private RouteDAO routeDAO;


    private Graph graph;

    @Autowired
    public BookingServiceImpl(BookingDAO bookingDAO, RouteDAO routeDAO, Graph graph) {
        this.routeDAO = routeDAO;
        this.bookingDAO = bookingDAO;
        this.graph = graph;
    }


    @Override
    public double calculatePrice(Booking booking) throws ServiceException {
        LOG.debug("Calculate price for new booking.");
        if (booking.getEntries().isEmpty()) {
            throw new ServiceException("Booking is empty!");
        }
        double sum = 0;

        for (BookingEntry e : booking.getEntries()) {

            if (e.getCustomer().getSocialNumber() != null) {
                sum += e.getRoute().getPrice() - (e.getRoute().getPrice() * 0.1);
            } else {
                sum += e.getRoute().getPrice();
            }
        }
        booking.setPrice(sum);
        return sum;
    }

    @Override
    public void updateCitiesBlindBooking(List<City> listToBeUpdated) throws ServiceException {
        try {
            bookingDAO.updateCitiesBlindBooking(listToBeUpdated);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void validateEntryForBlindBookingModification(List<String> entries) throws ServiceException {
        LOG.debug("Validating cities for blind booking modification");
        List<String> list;
        try {
            list = routeDAO.getAllCitiesNames();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        for (String d : entries) {
            if (!list.contains(d)) {
                throw new ServiceException("Please enter only valid cities.");
            }
        }
    }



    @Override
    public void createBooking(Booking booking) throws ServiceException {
        try {
            bookingDAO.createBooking(booking);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void createBookingEntry(Booking booking) throws ServiceException {
        try {
            bookingDAO.createBookingEntry(booking);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public boolean isReturnValid(Route r1, Route r2) throws ServiceException {
        LOG.debug("Validating if departure of r2 is after arrival of r1");
        if (r1 == null || r2 == null) {
            throw new ServiceException("Invalid parameters.");
        }
        return r2.getTimetable().getDepartureTime().isAfter(r1.getTimetable().getArrivalTime());
    }



    @Override
    public List<BookingEntry> loadBookingEntries() throws ServiceException {
        try {
            return bookingDAO.loadBookingEntries();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public List<Booking> loadBookings() throws ServiceException {
        try {
            return bookingDAO.loadBookings();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void cancel(Booking booking) throws ServiceException {
        LOG.debug("Cancelling the booking in service");
        LocalDateTime departure = booking.getEntries().get(0).getRoute().getTimetable().getDepartureTime();
        try {
            if (departure == null) {
                throw new ServiceException("Departure time can not be empty");
            }
            if (LocalDateTime.now().isAfter(departure.minusDays(1))) {
                throw new ServiceException("You cannot cancel the trip  in less than 24 hours before departure");
            } else {
               this.bookingDAO.deleteBooking(booking);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("Cannot cancel the booking, lost connection to services please try again latter",e.getCause());
        }

        LOG.debug("canceling the booking in service success");

    }

    @Override
    public List<Booking> filterBookings(String filter) throws ServiceException {
        List<Booking> bookingList = loadBookings();
        List<Booking> finalList = new ArrayList<>();
        if(filter==null){
            throw new ServiceException("Invalid parameter");
        }
        Long number = 0L;
        LOG.debug("SERVICE: Filtering bookings!");
        LOG.debug("Booking number: "+filter);
        try {
            number = Long.parseLong(filter);
        }catch (NumberFormatException e){
            throw new ServiceException("Invalid number");
        }
        for (Booking booking : bookingList){
            if (booking.getBookingNr().equals(number)){
                finalList.add(booking);
            }
        }
        return finalList;
    }


}
