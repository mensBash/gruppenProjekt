package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.TicketCounter;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class JDBCRouteDAO implements RouteDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ROUTE_INSERT_QUERY = "INSERT INTO Route (rid,bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES (default,?,?,?,?,?,?,?,?)";
    private static final String CITIES_INSERT_QUERY = "INSERT INTO Cities (name,country) VALUES (?,?)";
    private static final String GET_ALL_CITIES_QUERY = "SELECT * FROM Cities";
    private static final String GET_ALL_ROUTES_QUERY = "SELECT * FROM Route ORDER BY departure_time";
    private static final String GET_SEATS_QUERY = "SELECT * FROM Route r INNER JOIN BookingEntry be INNER JOIN Booking b ON r.rid = be.rid AND b.bid = be.bid WHERE r.rid = ? AND b.status = 'COMPLETED'";
    private static final String GET_BUS_NUMBERS = "SELECT DISTINCT bus_number FROM Route";
    private static final String ROUTE_UPDATE_QUERY = "UPDATE Route SET bus_number = ?, price = ?, distance = ? WHERE start_destination = ? AND end_destination = ?";
    private static final String GET_TIMETABLE_QUERY = "SELECT * FROM Route WHERE start_destination = ? AND end_destination =?";
    private static final String ROUTE_REMOVE_QUERY = "DELETE FROM Route WHERE start_destination = ? AND end_destination =? AND departure_time = ? AND arrival_time = ?";
    private static final String GET_ALL_FUTURE_ROUTES_QUERY = "SELECT * FROM Route WHERE departure_time > ?";
    private static final String GET_ALL_ROUTES_STATISTICS_QUERY = "SELECT * FROM Route r LEFT OUTER JOIN BookingEntry be on r.rid = be.rid WHERE r.departure_time > ? AND r.arrival_time < ?";

    private final PreparedStatement routeRemoveStatement;

    private Connection connection;

    @Autowired
    public JDBCRouteDAO(JDBCConnectionManager jdbcConnectionManager) throws PersistenceException {
        try {
            this.connection = jdbcConnectionManager.getConnection();

            routeRemoveStatement = connection.prepareStatement(ROUTE_REMOVE_QUERY);

        } catch (SQLException e) {
            throw new PersistenceException("Error in database", e);
        }
    }

    @Override
    public void createRoute(Route route) throws PersistenceException {
        LOG.debug("Adding route to DB {}", route);
        try {
            PreparedStatement routeInsertStatement = connection.prepareStatement(ROUTE_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);

            routeInsertStatement.setString(1, route.getBusNumber());
            routeInsertStatement.setDouble(2, route.getPrice());
            routeInsertStatement.setString(3, route.getStartDestination().getName());
            routeInsertStatement.setString(4, route.getEndDestination().getName());
            routeInsertStatement.setDouble(5, route.getDistance());
            routeInsertStatement.setInt(6, route.getCapacity());
            if (route.getTimetable() == null) {
                routeInsertStatement.setNull(7, Types.TIMESTAMP);
                routeInsertStatement.setNull(8, Types.TIMESTAMP);
            } else {
                routeInsertStatement.setTimestamp(7, Timestamp.valueOf(route.getTimetable().getDepartureTime()));
                routeInsertStatement.setTimestamp(8, Timestamp.valueOf(route.getTimetable().getArrivalTime()));
            }
            routeInsertStatement.executeUpdate();
            ResultSet generatedKeys = routeInsertStatement.getGeneratedKeys();
            generatedKeys.next();
            route.setRid(generatedKeys.getLong(1));

            generatedKeys.close();
            routeInsertStatement.close();

        } catch (SQLException | NullPointerException e) {
            LOG.error(e.getMessage());
            throw new PersistenceException("Route could not be created", e);
        }
    }

    @Override
    public void updateRoute(Route route) throws PersistenceException {
        LOG.debug("Updating route {}", route);
        try {
            PreparedStatement routeUpdateStatement = connection.prepareStatement(ROUTE_UPDATE_QUERY);

            routeUpdateStatement.setString(1, route.getBusNumber());
            routeUpdateStatement.setDouble(2, route.getPrice());
            routeUpdateStatement.setDouble(3, route.getDistance());
            routeUpdateStatement.setString(4, route.getStartDestination().getName());
            routeUpdateStatement.setString(5, route.getEndDestination().getName());
            routeUpdateStatement.executeUpdate();

            routeUpdateStatement.close();

        } catch (SQLException | NullPointerException e) {
            throw new PersistenceException("Route couldn't be updated", e);
        }
    }

    @Override
    public void updateTimetable(Route route, Map<Timetable, Double> timetableList) throws PersistenceException {
        LOG.debug("Updating route timetables");
        try {
            Map<Timetable, Double> initialList = new HashMap<>(loadTimetable(route));
            for (Map.Entry<Timetable, Double> t : timetableList.entrySet()) {
                if (!initialList.containsKey(t.getKey())) {
                    route.setTimetable(t.getKey());
                    route.setPrice(t.getValue());
                    createRoute(route);
                }
            }
            for (Map.Entry<Timetable, Double> t : initialList.entrySet()) {
                if (!timetableList.containsKey(t.getKey())) {
                    route.setTimetable(t.getKey());
                    route.setPrice(t.getValue());
                    deleteRoute(route);
                }
            }

        } catch (NullPointerException e) {
            throw new PersistenceException("null pointer in updateTimetable", e);
        }
    }

    @Override
    public void deleteRoute(Route route) throws PersistenceException {
        LOG.debug("Deleting route {}", route);
        try {
            routeRemoveStatement.setString(1, route.getStartDestination().getName());
            routeRemoveStatement.setString(2, route.getEndDestination().getName());
            routeRemoveStatement.setTimestamp(3, Timestamp.valueOf(route.getTimetable().getDepartureTime()));
            routeRemoveStatement.setTimestamp(4, Timestamp.valueOf(route.getTimetable().getArrivalTime()));
            routeRemoveStatement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Route couldn't be deleted.", e);
        }
    }


    @Override
    public void createCities(Route route, boolean startDest) throws PersistenceException {
        LOG.debug("Adding cities to the database");
        try {
            PreparedStatement citiesInsertStatement = connection.prepareStatement(CITIES_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);


            if (startDest) {
                citiesInsertStatement.setString(1, route.getStartDestination().getName());
                citiesInsertStatement.setString(2, route.getStartDestination().getCountry());
                citiesInsertStatement.executeUpdate();

            } else {
                citiesInsertStatement.setString(1, route.getEndDestination().getName());
                citiesInsertStatement.setString(2, route.getEndDestination().getCountry());
                citiesInsertStatement.executeUpdate();
            }

            citiesInsertStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by saving new city.", e);
        }

    }

    @Override
    public List<Integer> loadSeats(Route route) throws PersistenceException {
        LOG.debug("Loading unavailable seat from database");
        List<Integer> seatsStatus = new LinkedList<>();
        try {
            PreparedStatement getSeatsStatement = connection.prepareStatement(GET_SEATS_QUERY);

            getSeatsStatement.setLong(1, route.getRid());
            ResultSet rs = getSeatsStatement.executeQuery();
            while (rs.next()) {
                seatsStatus.add(rs.getInt("sid"));
            }

            rs.close();
            getSeatsStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by loading seat status info", e);
        }
        return seatsStatus;
    }

    @Override
    public List<Route> initializeGraph() throws PersistenceException {
        LOG.debug("Graph initialization");
        try {
            PreparedStatement getAllCitiesStatement = connection.prepareStatement(GET_ALL_CITIES_QUERY);

            ResultSet rs = getAllCitiesStatement.executeQuery();
            List<City> cities = new ArrayList<>();
            List<Route> routes = new ArrayList<>();
            while (rs.next()) {
                City c = new City(rs.getString("name"), rs.getString("country"));
                cities.add(c);
            }

            PreparedStatement getAllRoutestatement = connection.prepareStatement(GET_ALL_ROUTES_QUERY);


            rs = getAllRoutestatement.executeQuery();
            while (rs.next()) {
                Route r = new Route();
                r.setRid(rs.getLong("rid"));
                r.setBusNumber(rs.getString("bus_number"));
                r.setPrice(rs.getDouble("price"));
                r.setCapacity(rs.getInt("capacity"));
                r.setDistance(rs.getDouble("distance"));
                Timestamp departure = rs.getTimestamp("departure_time");
                Timestamp arrival = rs.getTimestamp("arrival_time");
                if (departure != null && arrival != null) {
                    Timetable t = new Timetable(
                        departure.toLocalDateTime(),
                        arrival.toLocalDateTime()
                    );
                    r.setTimetable(t);
                }

                for (City c : cities) {
                    if (c.getName().equals(rs.getString("start_destination"))) {
                        r.setStartDestination(c);
                    }
                    if (c.getName().equals(rs.getString("end_destination"))) {
                        r.setEndDestination(c);
                    }
                }

                routes.add(r);
            }

            rs.close();
            getAllCitiesStatement.close();
            getAllRoutestatement.close();

            return routes;
        } catch (SQLException e) {
            LOG.debug(e.getMessage());
            throw new PersistenceException("Error by initializing Graph!", e);
        }
    }

    @Override
    public List<City> takeCitiesToGraph() throws PersistenceException {
        LOG.debug("Graph initialization");
        List<City> cities = new ArrayList<>();
        try {
            PreparedStatement getAllCitiesStatement = connection.prepareStatement(GET_ALL_CITIES_QUERY);

            ResultSet rs = getAllCitiesStatement.executeQuery();
            while (rs.next()) {
                City c = new City(rs.getString("name"), rs.getString("country"));
                cities.add(c);
            }

            rs.close();
            getAllCitiesStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by initializing Graph!", e);
        }

        return cities;
    }

    @Override
    public Map<Timetable, Double> loadTimetable(Route route) throws PersistenceException {
        LOG.debug("Loading timetable from database");
        Map<Timetable, Double> timetableList = new HashMap<>();
        try {
            PreparedStatement getTimetableStatement = connection.prepareStatement(GET_TIMETABLE_QUERY);

            getTimetableStatement.setString(1, route.getStartDestination().getName());
            getTimetableStatement.setString(2, route.getEndDestination().getName());
            ResultSet rs = getTimetableStatement.executeQuery();
            while (rs.next()) {
                Timetable timetable = new Timetable(rs.getTimestamp("departure_time").toLocalDateTime(), rs.getTimestamp("arrival_time").toLocalDateTime());
                timetable.setId(rs.getLong("rid"));
                timetableList.put(timetable, rs.getDouble("price"));
            }

            rs.close();
            getTimetableStatement.close();
        } catch (SQLException e) {
            LOG.debug(e.getMessage());
            throw new PersistenceException("Error by loading timetable from database", e);
        }
        return timetableList;
    }

    @Override
    public List<String> getAllCitiesNames() throws PersistenceException {
        try {
            PreparedStatement getAllCitiesStatement = connection.prepareStatement(GET_ALL_CITIES_QUERY);

            ResultSet rs = getAllCitiesStatement.executeQuery();
            List<String> cities = new ArrayList<>();
            while (rs.next()) {
                City c = new City(rs.getString("name"), rs.getString("country"));
                cities.add(c.getName());
            }

            rs.close();
            getAllCitiesStatement.close();

            return cities;
        } catch (SQLException e) {
            throw new PersistenceException("Error by returning city list in DAO!", e);
        }

    }

    @Override
    public List<String> getAllCountriesName() throws PersistenceException {
        try {
            PreparedStatement getAllCitiesStatement = connection.prepareStatement(GET_ALL_CITIES_QUERY);

            ResultSet rs = getAllCitiesStatement.executeQuery();
            List<String> countries = new ArrayList<>();
            while (rs.next()) {
                City c = new City(rs.getString("name"), rs.getString("country"));
                if (!countries.contains(c.getCountry())) {
                    countries.add(c.getCountry());
                }
            }

            rs.close();
            getAllCitiesStatement.close();

            return countries;
        } catch (SQLException e) {
            throw new PersistenceException("Error by returning city list in DAO!", e);
        }

    }

    @Override
    public List<String> getAllBuses() throws PersistenceException {
        try {
            PreparedStatement getAllBuses = connection.prepareStatement(GET_BUS_NUMBERS);

            ResultSet rs = getAllBuses.executeQuery();
            List<String> buses = new ArrayList<>();
            while (rs.next()) {
                buses.add(rs.getString("bus_number"));
            }

            rs.close();
            getAllBuses.close();

            return buses;
        } catch (SQLException e) {
            throw new PersistenceException("Error by returning city list in DAO!", e);
        }

    }

    @Override
    public List<City> getAllCities() throws PersistenceException {
        ResultSet rs;
        List<City> cities = new ArrayList<>();
        try {
            PreparedStatement getAllCitiesStatement = connection.prepareStatement(GET_ALL_CITIES_QUERY);

            rs = getAllCitiesStatement.executeQuery();

            City city;

            while (rs.next()) {
                city = new City();
                city.setName(rs.getString("name"));
                city.setCountry(rs.getString("country"));
                city.setBlindBooking(rs.getBoolean("blind_booking"));
                city.setPicture(rs.getString("picture"));
                cities.add(city);
            }

            rs.close();
            getAllCitiesStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by retrieving all cities from database!", e);
        }

        return cities;
    }

    @Override
    public int getNumberAllFutureRoutes() throws PersistenceException {
        LOG.debug("Counting all future routes from database");
        int numberOfRoutes = 0;
        try {
            PreparedStatement getAllFutureRoutesStatement = connection.prepareStatement(GET_ALL_FUTURE_ROUTES_QUERY);
            getAllFutureRoutesStatement.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
            ResultSet rs = getAllFutureRoutesStatement.executeQuery();
            while (rs.next()){
                numberOfRoutes++;
            }
            rs.close();
            getAllFutureRoutesStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by retrieving all routes from database!",e);
        }
        return numberOfRoutes;
    }

    @Override
    public List<TicketCounter> getNumberOfTicketsPerRoute(LocalDateTime from, LocalDateTime to) throws PersistenceException {
        LOG.debug("Counting number of tickets per route in a selected period");
        List<TicketCounter> ticketsPerRoute = new ArrayList<>();
        try {
            PreparedStatement getAllRoutesStatisticsStatement = connection.prepareStatement(GET_ALL_ROUTES_STATISTICS_QUERY);
            getAllRoutesStatisticsStatement.setTimestamp(1, Timestamp.valueOf(from));
            getAllRoutesStatisticsStatement.setTimestamp(2, Timestamp.valueOf(to));
            ResultSet rs = getAllRoutesStatisticsStatement.executeQuery();
            while(rs.next()){
                boolean alreadyFound = false;
                String startDestination = rs.getString("start_destination");
                String endDestination = rs.getString("end_destination");
                Long bookingID = rs.getLong("bid");
                for(int i = 0; i < ticketsPerRoute.size(); i++){
                    if (startDestination.equals(ticketsPerRoute.get(i).getDepartureCity()) && endDestination.equals(ticketsPerRoute.get(i).getArrivalCity())) {
                        if(bookingID != 0){
                           ticketsPerRoute.get(i).setOneWayTicketsCounter(ticketsPerRoute.get(i).getOneWayTicketsCounter()+1);
                        }
                        alreadyFound = true;
                    } else if (startDestination.equals(ticketsPerRoute.get(i).getArrivalCity()) && endDestination.equals(ticketsPerRoute.get(i).getDepartureCity())) {
                        if(bookingID != 0){
                            ticketsPerRoute.get(i).setReturnTicketsCounter(ticketsPerRoute.get(i).getReturnTicketsCounter()+1);
                        }
                        alreadyFound = true;
                    }
                }
                if(!alreadyFound){
                    TicketCounter ticketCounter = new TicketCounter(startDestination, endDestination, 0, 0);
                    if(bookingID != 0) {
                        ticketCounter.setOneWayTicketsCounter(1);
                    }
                    ticketsPerRoute.add(ticketCounter);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error retrieving all routes and booking entries from database",e);
        }
        return ticketsPerRoute;
    }

}
