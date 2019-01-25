package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.TicketCounter;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RouteDAO {

    /**
     * Create new route with specification given from worker,
     * storing the new route in database
     * @param route instance of route that contains all data that are needed to store route
     * @throws PersistenceException thrown when route cannot be stored
     */
    void createRoute(Route route) throws PersistenceException;

    /**
     * Updating the route from the argument
     * @param route instance of route to be updated, containing new changeable values of route
     * @throws PersistenceException thrown when route cannot be updated
     */
    void updateRoute(Route route) throws PersistenceException;

    /**
     * Saving new and deleting non-existing timetables for the certain route
     * @param route instance of route that contains start and end destination
     * @param timetableList list of current timetables for the route
     * @throws PersistenceException thrown when timetables cannot be updated
     */
    void updateTimetable(Route route, Map<Timetable, Double> timetableList) throws PersistenceException;

    /**
     * Deleting existing route
     * @param route the instance of route to be deleted, containing arrival and departure date
     *              as well as start and end destination
     * @throws PersistenceException thrown when route cannot be deleted
     */
    void deleteRoute(Route route) throws PersistenceException;

    /**
     * Storing departure and arrival cities from route in database
     * @param route instance of route that contains both departure and arrival city
     * @param startDest boolean variable which checks if start or end destination should be stored
     * @throws PersistenceException thrown when city cannot be stored
     */
    void createCities(Route route, boolean startDest) throws PersistenceException;

    /**
     * Loads numbers of seats that are already booked for a selected route
     * @param route selected route, for which unavailable seats have to be displayed
     * @return list of integers that represent booked (unavailable) seat numbers
     * @throws PersistenceException thrown when required info couldn't be loaded from bookingEntry or route table in database
     */
    List<Integer> loadSeats(Route route) throws PersistenceException;

    /**
     * Graph initialization at application start
     * @return map which contains list of routes and list of cities needed to initialize graph
     * @throws PersistenceException thrown when cities or routes cannot be loaded from database
     */
    List<Route> initializeGraph() throws PersistenceException;

    List<City> takeCitiesToGraph() throws PersistenceException;

    /**
     * Loads timetable and price for the selected route and puts it in the map
     * @param route selected route, from which timetable wants to be displayed
     * @return map with the timetable (departure and arrival time) as a key and price as value for the selected route
     * @throws PersistenceException thrown when information about route's timetable and price couldn't be loaded from route table in database
     */
    Map<Timetable,Double> loadTimetable(Route route) throws PersistenceException;



    /**
     * Function that returns all cities that are being offered as destinations
     * @return List of all cities in Database
     * @throws PersistenceException throws when list cannot be returned
     */
    List<String> getAllCitiesNames() throws PersistenceException;

    /**
     * Function that returns all countries that are being offered as destinations
     * @return List of all countries in Database
     * @throws PersistenceException throws when list cannot be returned
     */
    List<String> getAllCountriesName() throws PersistenceException;

    /**
     * Function that returns all buses that are being used for destinations
     * @return List of all buses in Database
     * @throws PersistenceException throws when list cannot be returned
     */
    List<String> getAllBuses() throws PersistenceException;


    /**
     * Function that returns all cities that are stored in database as objects
     * @return List of all cities in database
     * @throws PersistenceException throws when list cannot be restored from database
     */
    List<City> getAllCities() throws PersistenceException;

    /**
     * Method that counts a number of all future routes stored in database
     * @return number of future routes in database
     * @throws PersistenceException throws when routes cannot be loaded from database
     */
    int getNumberAllFutureRoutes() throws PersistenceException;

    /**
     * Methods that returns a list of routes together with the number of tickets sold in the selected period
     * @param from start datetime from which the routed are considered
     * @param to end datetime from which the routes are considered
     * @return return list of routes together with the number of tickets for both direction
     * @throws PersistenceException throws when routes or booking entry cannot be loaded from database
     */
    List<TicketCounter> getNumberOfTicketsPerRoute(LocalDateTime from, LocalDateTime to) throws PersistenceException;
}
