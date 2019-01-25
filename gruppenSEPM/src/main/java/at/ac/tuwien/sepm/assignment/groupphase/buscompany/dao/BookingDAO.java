package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Booking;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.BookingEntry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;

import java.time.LocalDate;
import java.util.List;

public interface BookingDAO {


    /**
     * Creating new booking and storing in datatbase
     * @param booking object that contains all needed data about one booking when we want him to store in database
     * @throws PersistenceException thrown when booking cannot be saved in database
     */
    void createBooking(Booking booking) throws PersistenceException;

    /**
     * Updating list of cities that are going to be offered in blind booking
     * @param listToBeUpdated list of city to be updated
     * @throws PersistenceException thrown when cities cannot be updated
     */
    void updateCitiesBlindBooking(List<City> listToBeUpdated) throws PersistenceException;

    /**
     * Creating single booking entries, so if one booking consists of more persons for one booking
     * @param booking booking to be saved
     * @throws PersistenceException thrown when this booking entry cannot be stored in database
     */
    void createBookingEntry(Booking booking) throws PersistenceException;

    /**
     * Loads all booking entries from database in a list
     * @return list of all booking entries
     * @throws PersistenceException thrown when booking entries couldn't be loaded from booking entry table in database
     */
    List<BookingEntry> loadBookingEntries() throws PersistenceException;

    /**
     * Loads all bookings from database in a list
     * @return list of all stored bookings
     * @throws PersistenceException thrown when booking couldn't be loaded from booking table in database
     */
    List<Booking> loadBookings() throws PersistenceException;

    /**
     * Function that returns total income for a certain route in a given time period
     * @param route the instance of route for which the total income is to be calculated
     * @param dateFrom the date from which the sum is to be calculated
     * @param dateTo the date until which the sum is to be calculated
     * @return the total income for a certain route for a given time period
     * @throws PersistenceException thrown when the prices for route cannot be loaded from database
     */
    int getPriceForRoute(Route route, LocalDate dateFrom, LocalDate dateTo) throws PersistenceException;

    /**
     * Function that return number of booked seats for a certain date
     * @param date date for which the calculation of booked seats should be done
     * @return the number of booked seats for a given date
     * @throws PersistenceException theown when the number of booked seats cannot be loaded
     */
    int getNumberOfBookedSeats(LocalDate date) throws PersistenceException;

    /**
     * Method that counts the number of all future booking entries stored in database
     * @return number of all future entries
     * @throws PersistenceException throws when booking entries cannot be loaded from database
     */
    int getNumberAllFutureBookingEntries() throws PersistenceException;

    /**
     * Method that count the number of all future bookings stored in database
     * @return number of future bookings
     * @throws PersistenceException throws when bookings, booking entries or routes cannot be loaded from database
     */
    int getNumberAllFutureBookings() throws PersistenceException;

    /**
     * Deleting booking from database
     * @param booking booking to be deleted
     * @throws PersistenceException thrown when booking info couldn't be loaded from database
     */
    void deleteBooking(Booking booking) throws PersistenceException;
}
