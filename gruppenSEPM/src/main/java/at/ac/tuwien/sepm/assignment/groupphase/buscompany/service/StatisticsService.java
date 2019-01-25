package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.TicketCounter;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticsService {

    /**
     * Method that counts number of all future completed bookings
     * @return number of future bookings
     * @throws ServiceException thrown when booking entries  couldn't be loaded from database
     */
    int countActiveBookings() throws ServiceException;

    /**
     * Method that counts number of all registered customers
     * @return numer of registered cutomers
     * @throws ServiceException thrown when customers couldn't be loaded from database
     */
    int countRegisteredCustomers() throws  ServiceException;

    /**
     * Method that calculates the percentage of booked (unavailable) seats in future routes
     * @return percent of how full is the capacity of all buses
     * @throws ServiceException thrown when booking entries or routes couldn't be loaded from database
     */
    double calculateCapacityUtilization() throws ServiceException;

    /**
     * Methods that returns a list of routes together with the number of tickets sold in the selected period
     * @param from start datetime from which the routed are considered
     * @param to end datetime from which the routes are considered
     * @return return list of routes together with the number of tickets for both direction
     * @throws ServiceException throws when routes or booking entry cannot be loaded from database
     */
    List<TicketCounter> getNumberOfTicketsPerRoute(LocalDateTime from, LocalDateTime to) throws ServiceException;

    /**
     * Method that returns list of all simple routes (onward and backward as single route) from a graph
     * @return the list of all existing routes
     */
    List<Route> getRoutes();

    /**
     * Methjod that returns total income for a certain route in a given time period
     * @param route the instance of route for which the total income is to be calculated
     * @param dateFrom the date from which the sum is to be calculated
     * @param dateTo the date until which the sum is to be calculated
     * @return the total income for a certain route for a given time period
     * @throws ServiceException thrown when the prices for route cannot be loaded from database
     */
    int getPriceForRoute(Route route, LocalDate dateFrom, LocalDate dateTo) throws ServiceException;

    /**
     * Function that return number of booked seats for a certain date
     * @param date date for which the calculation of booked seats should be done
     * @return the number of booked seats for a given date
     * @throws ServiceException theown when the number of booked seats cannot be loaded
     */
    int getNumberOfBookedSeats(LocalDate date) throws ServiceException;


}
