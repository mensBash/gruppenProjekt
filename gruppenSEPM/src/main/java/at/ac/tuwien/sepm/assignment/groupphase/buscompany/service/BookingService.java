package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Booking;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.BookingEntry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking.CreateBookingController;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    /**
     * this method calculate a discount of 10% for each route a premium costumer has booked,
     * otherwise calculate the actual price.
     *
     * @param booking include all information about a routes, costumers and time tables.
     * @return total price after calculating the discount of premium costumers.
     * @throws ServiceException when a booking.getEntries() is empty.
     */
    double calculatePrice(Booking booking) throws ServiceException;

    /**
     * When creating a new booking
     * @param booking booking that we want to create
     * @throws ServiceException thrown when booking cannot be stored
     */
    void createBooking(Booking booking) throws ServiceException;

    /**
     * Create specific booking entry if one booking consists more persons
     * @param booking booking entry that we want to store
     * @throws ServiceException thrown when booking entry cannot be stored
     */
    void createBookingEntry(Booking booking) throws ServiceException;

    /**
     * Validating if departure of one route (r2) is after arrival of another(r1)
     * @param r1 route that we want to check arrival time
     * @param r2 route that we want to check departure time
     * @return true if departure time of r2 is after arrival time of r1
     * @throws ServiceException throws null if one of route or both are null
     */
    boolean isReturnValid(Route r1, Route r2) throws ServiceException;

    /**
     * Methode call when we want to update list of cities that are going to be offered in blind booking
     * @param listToBeUpdated list of cities that we want to update
     * @throws ServiceException thrown when persistence layer cannot update a list
     */
    void updateCitiesBlindBooking(List<City> listToBeUpdated) throws ServiceException;

    /**
     * Called as part of validation when we want to change current list of cities in blind booking
     * @param entries list of entries that we want change
     * @throws ServiceException thrown when one of city is not known, or better said not stored in database
     */
    void validateEntryForBlindBookingModification(List<String> entries) throws ServiceException;

    /**
     * Method that return all booking entries which contain route and seat ids
     * @return list of booking entries that contain route id and reat id
     * @throws ServiceException thrown when the booking entries cannot be loaded from the database
     */
    List<BookingEntry>  loadBookingEntries() throws ServiceException;

    /**
     * Method that returns all the booking from database
     * @return List of all booking in the database
     * @throws ServiceException thrown when bookings cannot be loaded from the database
     */
    List<Booking> loadBookings() throws ServiceException;

    /**
     * Called when we want to cancel a booking
     * @param booking booking to be canceled
     * @throws ServiceException when persistence layer cannot delete a booking
     */
    void cancel(Booking booking) throws ServiceException;

    List<Booking> filterBookings(String filter) throws ServiceException;
}
