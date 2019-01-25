package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.FareItem;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking.CreateBookingController;
import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidRouteException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RouteService {

    /**
     * Creates new route into graph and into database, propagating to DAO layer
     * @param r - route object to be created
     * @throws ServiceException - thrown when route cannot be stored
     * @throws InvalidRouteException - thrown when route is not valid
     */
    void addRoute(Route r) throws ServiceException,InvalidRouteException;

    /**
     * Updating the route from the argument and reset the graph
     * @param route instance of route to be updated, containing new changeable values of route
     * @throws ServiceException  thrown when route cannot be updated
     * @throws InvalidRouteException thrown when route is not valid
     */
    void updateRoute(Route route) throws ServiceException,InvalidRouteException;

    /**
     * Saving new and deleting non-existing timetables for the certain route and reset the graph
     * @param route instance of route that contains start and end destination
     * @param timetableList list of current timetables for the route
     * @throws ServiceException thrown when timetables cannot be updated
     */
    void updateTimetable(Route route, Map<Timetable, Double> timetableList) throws ServiceException;

    /**
     * Check if the timetable have overlapping with other timetable entries in the collection
     * @param timetable timetable instance to verify
     * @param timetableList collection of timetables to be checked overlap with
     * @throws ServiceException thrown if timetable have overlap with any of timetableList collection
     */
    void checkOverlap(Timetable timetable, Collection<Timetable> timetableList) throws ServiceException;

    /**
     * Methode call when we want load all seats of one specific route
     * @param route that specific route
     * @return returns list of all booked seats
     * @throws ServiceException thrown when seats cannot be loaded
     */
    List<Integer> loadSeats(Route route) throws ServiceException;

    /**
     * Function that initializes graph with data from database.
     * @throws ServiceException thrown if graph couldn't be initialized
     */
    void initializeGraph() throws ServiceException;


    /**
     * Function that returns all cities that are being offered as destinations
     * @return List of all cities in Database
     * @throws ServiceException throws when list cannot be returned
     */
    List<String> getAllCitiesNames() throws ServiceException;

    /**
     * Function that returns all countries that are being offered as destinations
     * @return List of all countries in Database
     * @throws ServiceException throws when list cannot be returned
     */
    List<String> getAllCountriesName() throws ServiceException;

    /**
     * Function that returns all buses that are used for destinations
     * @return List of all buses in Database
     * @throws ServiceException throws when list cannot be returned
     */
    List<String> getAllBuses() throws ServiceException;

    /**
     * Function that returns all simple routes.(connection between two cities)
     * @return All simple connections in graph
     * @throws ServiceException throws when list cannot be returned
     */
    List<Route> getAllSimpleRoutes() throws ServiceException;

    /**
     * Filter all simple routes of one route
     * @param filter route that we want to filter
     * @return return list of those routes
     * @throws ServiceException
     */
    List<Route> filterSimpleRoutes(Route filter) throws ServiceException;


    /**
     * Get all timetables of one specific route
     * @param route that specific route
     * @return map of all timetables, keys are departure and arrival time, and values are prices
     * @throws ServiceException thrown when timetables cannot be loaded
     */
    Map<Timetable,Double> loadTimetable(Route route) throws ServiceException;

    /**
     * Function that calculates price for list of routes
     * @param routes list of routes for which price should be calculated
     * @return total price
     * @throws ServiceException thrown if price couldn't be calculated
     */
    double calculatePrice(List<Route> routes) throws ServiceException;
    /**
     * Function that calculates distance for list of routes
     * @param routes list of routes for which distance should be calculated
     * @return total distance
     * @throws ServiceException thrown if distance couldn't be calculated
     */
    double calculateDistance(List<Route> routes) throws ServiceException;

    /**
     * Functions that checks if a route is booked.
     * @param timetable - timetable of route that needs to be checked
     * @return list of booking numbers in which this route exists
     * @throws ServiceException thrown if bookings cannot be retrieved,so route can not be checked
     */
    List<Long> isBooked(Timetable timetable) throws ServiceException;


    /**
     * Function that returns all cities that are stored in database as objects
     * @return List of all cities in database
     * @throws ServiceException throws when list cannot be restored from database
     */
    List<City> getAllCities() throws ServiceException;


    /**
     * Function that returns fares for routes between two cities order by month,after specific date.
     * If second city is null it will return cheapest fares for all routes,in which start destination is first city.
     * @param city - start destination
     * @param city1 - end destination
     * @param departure - only routes after this date will be considered
     * @return - map of FareItem(City and Month) and description of the route for that city in this month
     * @throws ServiceException throws when list of routes between two cities cannot be retrieved
     * @throws NotFoundException thrown if there aren't any routes that satisfy this criteria
     */
    Map<FareItem, Route> findFares(City city, City city1, LocalDate departure) throws ServiceException,NotFoundException;

    /**
     * Function that returns all routes for one month between two cities.
     * @param r - Route which contains start,end destination and departure time(month)
     * @return -map which contains all routes paired with one day in a month
     * @throws ServiceException thrown if list of routes cannot be retrieved
     */
    Map<LocalDate,Route> getRoutesForMonth(Route r) throws ServiceException;

    /**
     * Function that returns list of routes on specific date.
     * @param searchRoute - criteria for searching routes
     * @param returnTicket - true if the route is return route
     * @param sort - criteria for sorting found routes
     * @param limit - true if number of found routes should be limited
     * @return list of chained routes
     * @throws ServiceException thrown if list of routes cannot be retrieved
     */
    List<List<Route>> searchRoutes(Route searchRoute, boolean returnTicket, String sort,boolean limit) throws ServiceException;

}
