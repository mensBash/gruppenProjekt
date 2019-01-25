package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.JDBCConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Repository
public class JDBCBookingDAO implements BookingDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String BOOKING_INSERT_QUERY = "INSERT INTO Booking(bid, time_of_booking, booking_number, status, total_sum) VALUES (default, ?, ?, ?, ?)";
    private static final String BOOKING_ENTRY_INSERT_QUERY = "INSERT INTO BookingEntry(bid, sid, rid, name, surname) VALUES (?,?,?,?,?)";
    private static final String GET_ALL_ENTRIES_QUERY = "SELECT * FROM  BookingEntry";
    private static final String UPDATE_BLINDBOOKING_CITIES = "UPDATE CITIES SET blind_booking = ?, picture = ? where name = ?";
    private static final String GET_ALL_FUTURE_ENTRIES_QUERY = "SELECT * FROM  BookingEntry be INNER JOIN Route r ON be.rid = r.rid WHERE r.departure_time > ?";
    private static final String GET_ALL_BOOKINGS = "SELECT * FROM Booking";
    private static final String GET_ALL_BOOKINGS_AND_ENTRIES = "SELECT * FROM BOOKING b INNER JOIN BOOKINGENTRY be on b.bid = be.bid INNER JOIN ROUTE r on r.rid = be.rid";
    private static final String GET_ALL_BOOKED_ENTRIES_QUERY = "SELECT * FROM  BookingEntry be INNER JOIN Route r ON be.rid = r.rid WHERE bid = ? AND r.departure_time > ?";
    private static final String DELETE_BOOKING = "DELETE FROM Booking WHERE bid = ?";
    private static final String GET_PRICES_FOR_EACH_ENTRY = "SELECT r.price FROM BookingEntry be INNER JOIN Route r ON be.rid = r.rid INNER JOIN Booking b on be.bid = b.bid WHERE r.start_destination = ? AND r.end_destination = ? AND departure_time > ? AND arrival_time < ?";
    private static final String GET_NUMBER_OF_BOOKED_SEATS = "SELECT COUNT(*) AS number FROM BookingEntry be INNER JOIN Route r ON be.rid = r.rid INNER JOIN Booking b on be.bid = b.bid WHERE departure_time > ? AND arrival_time < ?";


    private Connection connection;

    @Autowired
    public JDBCBookingDAO(JDBCConnectionManager jdbcConnectionManager) throws PersistenceException {
        try {
            this.connection = jdbcConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }


    @Override
    public void createBooking(Booking booking) throws PersistenceException {
        LOG.debug("Adding booking to DB {}", booking);
        try {
            PreparedStatement bookingInsertStatement = this.connection.prepareStatement(BOOKING_INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);

            bookingInsertStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            bookingInsertStatement.setLong(2, booking.getBookingNr());
            bookingInsertStatement.setString(3, String.valueOf(booking.getStatus()));
            bookingInsertStatement.setDouble(4, booking.getPrice());
            bookingInsertStatement.executeUpdate();
            ResultSet generatedKeys = bookingInsertStatement.getGeneratedKeys();
            generatedKeys.next();
            booking.setBid(generatedKeys.getLong(1));

            generatedKeys.close();
            bookingInsertStatement.close();

        } catch (SQLException | NullPointerException e) {
            throw new PersistenceException("Booking could not be created", e);
        }
    }

    @Override
    public void updateCitiesBlindBooking(List<City> listToBeUpdated) throws PersistenceException {
        try {
            PreparedStatement updateBlindBookingCities = this.connection.prepareStatement(UPDATE_BLINDBOOKING_CITIES);
            copyImages(listToBeUpdated);
            for (City aListToBeUpdated : listToBeUpdated) {
                updateBlindBookingCities.setBoolean(1, aListToBeUpdated.isBlindBooking());
                updateBlindBookingCities.setString(2, aListToBeUpdated.getPicture());
                updateBlindBookingCities.setString(3, aListToBeUpdated.getName());
                updateBlindBookingCities.executeUpdate();
            }

            updateBlindBookingCities.close();
        } catch (SQLException e) {
            throw new PersistenceException("Cities could not be updated.", e);
        }
    }

    private void copyImages(List<City> list) throws PersistenceException {
        try {
            for (City c : list) {
                if (c.getPicture() != null && !c.getPicture().isEmpty()) {
                    File file = new File(c.getPicture());
                    File coppiedPhoto = new File("src/main/resources/images/" + c.getName() + ".jpg");
                    Files.copy(file.toPath(), coppiedPhoto.toPath(), REPLACE_EXISTING);

                    LOG.info("User chose file {}", coppiedPhoto.getAbsolutePath());
                    c.setPicture(coppiedPhoto.getPath());
                } else {
                    c.setPicture(null);
                }

            }

        } catch (NullPointerException | IOException e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public void createBookingEntry(Booking booking) throws PersistenceException {
        LOG.debug("Adding all entries of booking to DB {}", booking);
        try {

            PreparedStatement bookingEntryInsertStatement = connection.prepareStatement(BOOKING_ENTRY_INSERT_QUERY);

            bookingEntryInsertStatement.setLong(1, booking.getBid());
            for (BookingEntry bookingEntry : booking.getEntries()) {
                bookingEntryInsertStatement.setLong(2, bookingEntry.getSeatNo());
                bookingEntryInsertStatement.setLong(3, bookingEntry.getRoute().getRid());
                bookingEntryInsertStatement.setString(4, bookingEntry.getCustomer().getFirstName());
                bookingEntryInsertStatement.setString(5, bookingEntry.getCustomer().getLastName());
                bookingEntryInsertStatement.executeUpdate();
            }

            bookingEntryInsertStatement.close();

        } catch (SQLException | NullPointerException e) {
            throw new PersistenceException("Booking entry could not be created", e);
        }
    }

    @Override
    public List<BookingEntry> loadBookingEntries() throws PersistenceException {
        LOG.debug("Getting all booking entries");
        List<BookingEntry> entryList = new ArrayList<>();
        try {

            PreparedStatement getAllEntriesStatement = connection.prepareStatement(GET_ALL_ENTRIES_QUERY);
            ResultSet rs = getAllEntriesStatement.executeQuery();

            while (rs.next()) {
                Route route = new Route();
                route.setRid(rs.getLong("rid"));
                BookingEntry bookingEntry = new BookingEntry(null, rs.getInt("sid"), route);
                entryList.add(bookingEntry);
            }

            rs.close();
            getAllEntriesStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Entries couldn't be loaded", e);
        }
        return entryList;
    }

    @Override
    public List<Booking> loadBookings() throws PersistenceException {
        LOG.debug("Getting all booking data");
        List<Booking> bookingList = new ArrayList<>();
        try {
            PreparedStatement getAllBookingsStatement = connection.prepareStatement(GET_ALL_BOOKINGS);
            PreparedStatement getAllBookedEntriesStatement = connection.prepareStatement(GET_ALL_BOOKED_ENTRIES_QUERY);

            ResultSet rs = getAllBookingsStatement.executeQuery();

            while (rs.next()) {
                //Booking
                Booking b = new Booking();
                b.setBid(rs.getLong("bid"));
                b.setBookingNr(rs.getLong("booking_number"));
                b.setPrice(rs.getDouble("total_sum"));
                if (rs.getString("status").toUpperCase().equals("COMPLETED")) {
                    b.setStatus(Booking.Status.COMPLETED);
                } else {
                    b.setStatus(Booking.Status.CANCELLED);
                }
                b.setTimeOfBooking(rs.getTimestamp("time_of_booking").toLocalDateTime());
                getAllBookedEntriesStatement.setLong(1, b.getBid());
                getAllBookedEntriesStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ResultSet rs1 = getAllBookedEntriesStatement.executeQuery();
                List<BookingEntry> entryList = new ArrayList<>();
                while (rs1.next()) {
                    //Route
                    Route r = new Route();
                    r.setRid(rs1.getLong("rid"));
                    r.setBusNumber(rs1.getString("bus_number"));
                    r.setPrice(rs1.getDouble("price"));
                    r.setCapacity(rs1.getInt("capacity"));
                    r.setDistance(rs1.getDouble("distance"));
                    Timestamp departure = rs1.getTimestamp("departure_time");
                    Timestamp arrival = rs1.getTimestamp("arrival_time");
                    r.setTimetable(new Timetable(departure.toLocalDateTime(), arrival.toLocalDateTime()));
                    City cityStart = new City();
                    City cityEnd = new City();
                    cityStart.setName(rs1.getString("start_destination"));
                    r.setStartDestination(cityStart);
                    cityEnd.setName(rs1.getString("end_destination"));
                    r.setEndDestination(cityEnd);
                    Customer customer = new Customer();
                    customer.setFirstName(rs1.getString("name"));
                    customer.setLastName(rs1.getString("surname"));
                    BookingEntry bookingEntry = new BookingEntry(customer,
                        rs1.getInt("sid"),
                        r
                    );
                    entryList.add(bookingEntry);
                }
                if (!entryList.isEmpty()) {
                    b.setEntries(entryList);
                    bookingList.add(b);
                }
            }

            rs.close();
            getAllBookingsStatement.close();
            getAllBookedEntriesStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Bookings couldn't be loaded", e);
        }
        return bookingList;
    }

    @Override
    public int getPriceForRoute(Route route, LocalDate dateFrom, LocalDate dateTo) throws PersistenceException {
        LOG.debug("Calculating total profit for route in the selected period ");
        int price = 0;
        try {
            PreparedStatement getPricesForEachEntry = connection.prepareStatement(GET_PRICES_FOR_EACH_ENTRY);
            getPricesForEachEntry.setString(1, route.getStartDestination().getName());
            getPricesForEachEntry.setString(2, route.getEndDestination().getName());
            getPricesForEachEntry.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(dateFrom, LocalTime.of(0,0))));
            getPricesForEachEntry.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(dateTo, LocalTime.of(23,59))));
            ResultSet rs = getPricesForEachEntry.executeQuery();
            while (rs.next()){
                price += rs.getInt("price");
            }
            getPricesForEachEntry.setString(1, route.getEndDestination().getName());
            getPricesForEachEntry.setString(2, route.getStartDestination().getName());
            getPricesForEachEntry.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(dateFrom, LocalTime.of(0,0))));
            getPricesForEachEntry.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(dateTo, LocalTime.of(23,59))));
            rs = getPricesForEachEntry.executeQuery();
            while (rs.next()){
                price += rs.getInt("price");
            }
            rs.close();
            getPricesForEachEntry.close();
            return price;
        } catch (SQLException e) {
            throw new PersistenceException("Error while retrieving prices for routes", e);
        }
    }

    @Override
    public int getNumberOfBookedSeats(LocalDate date) throws PersistenceException {
        LOG.debug("Loading booked seats for routes according to given date");
        int numberOfSeats = 0;
        try {
            PreparedStatement getNumberOfBookedSeats = connection.prepareStatement(GET_NUMBER_OF_BOOKED_SEATS);
            getNumberOfBookedSeats.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(date, LocalTime.of(0,0))));
            getNumberOfBookedSeats.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(date, LocalTime.of(23,59))));
            ResultSet rs = getNumberOfBookedSeats.executeQuery();
            while (rs.next()){
                numberOfSeats += rs.getInt("number");
            }
            rs.close();
            getNumberOfBookedSeats.close();
            return numberOfSeats;
        } catch (SQLException e){
            throw new PersistenceException("Error while retrieving number of booked seats", e);
        }
    }

    @Override
    public int getNumberAllFutureBookingEntries() throws PersistenceException {
        LOG.debug("Counting all future booking entries from database");
        int numberOfEntries = 0;
        try {
            PreparedStatement getAllFutureBookingEntriesStatement = connection.prepareStatement(GET_ALL_FUTURE_ENTRIES_QUERY);
            getAllFutureBookingEntriesStatement.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
            ResultSet rs = getAllFutureBookingEntriesStatement.executeQuery();
            while (rs.next()){
                numberOfEntries++;
            }
            rs.close();
            getAllFutureBookingEntriesStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by retrieving booking entries from database!",e);
        }
        return numberOfEntries;
    }

    @Override
    public int getNumberAllFutureBookings() throws PersistenceException {
        LOG.debug("Counting all future bookings from database");
        List<Long> listOfBookingIds = new ArrayList<>();
        try {
            PreparedStatement getAllBookingsAndEntriesStatement = connection.prepareStatement(GET_ALL_BOOKINGS_AND_ENTRIES);
            ResultSet rs = getAllBookingsAndEntriesStatement.executeQuery();
            while (rs.next()){
                long id = rs.getLong("bid");
                Timestamp departure = rs.getTimestamp("departure_time");
                if(!listOfBookingIds.contains(id) && departure.after(new Timestamp(System.currentTimeMillis()))){
                    listOfBookingIds.add(id);
                }else if(listOfBookingIds.contains(id) && !departure.after(new Timestamp(System.currentTimeMillis()))){
                    listOfBookingIds.remove(id);
                }
            }
            rs.close();
            getAllBookingsAndEntriesStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by retrieving bookings, booking entries and routes from database!",e);
        }
        return listOfBookingIds.size();
    }

    @Override
    public void deleteBooking(Booking booking) throws PersistenceException {
        try {
            PreparedStatement deleteBookingStatement = connection.prepareStatement(DELETE_BOOKING);
            deleteBookingStatement.setLong(1,booking.getBid());
            deleteBookingStatement.executeUpdate();
            deleteBookingStatement.close();
        } catch (SQLException e) {
            throw new PersistenceException("Error by deleting booking from database");
        }
    }

}
