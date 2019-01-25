package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.BookingDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.CustomerDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.*;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PersistenceException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private BookingDAO bookingDAO;
    private RouteDAO routeDAO;
    private CustomerDAO customerDAO;

    @Autowired
    private Graph graph;

    @Autowired
    public StatisticsServiceImpl(BookingDAO bookingService, RouteDAO routeService, CustomerDAO customerService){
        this.bookingDAO = bookingService;
        this.routeDAO = routeService;
        this.customerDAO = customerService;
    }

    @Override
    public int countActiveBookings() throws ServiceException{
        int numberOfBookings;
        try {
            numberOfBookings = bookingDAO.getNumberAllFutureBookings();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return numberOfBookings;
    }

    @Override
    public int countRegisteredCustomers() throws ServiceException {
        List<Customer> customers;
        try {
            customers = customerDAO.loadCustomers();
        } catch (CustomerException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return customers.size();
    }

    @Override
    public double calculateCapacityUtilization() throws ServiceException {
        LOG.debug("Calculating capacity utilization");
        double capacityFull;
        int numOfBookedSeats;
        int numOfBookingEntries;
        try {
            numOfBookedSeats = routeDAO.getNumberAllFutureRoutes() * 53;
            numOfBookingEntries = bookingDAO.getNumberAllFutureBookingEntries() ;
            capacityFull = (float)numOfBookingEntries/numOfBookedSeats*100;
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return (double)Math.round(capacityFull*10)/10;
    }

    @Override
    public List<TicketCounter> getNumberOfTicketsPerRoute(LocalDateTime from, LocalDateTime to) throws ServiceException {
        LOG.debug("Counting number of tickets per route ad sorting them from largest number to smallest");
        List<TicketCounter> numberOfTickets;
        try {
            numberOfTickets = routeDAO.getNumberOfTicketsPerRoute(from, to);
            Collections.sort(numberOfTickets, (o1, o2) -> o1.compareTo(o2));
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return numberOfTickets;
    }

    @Override
    public List<Route> getRoutes(){
        LOG.debug("Returning all routes from the graph");
        List<Map<String, String>> mapList = new ArrayList<>();
        List<Route> routeList = new ArrayList<>();
        for (Route route : graph.getGraph().edgeSet()){
            Map mapHelp1 = new HashMap();
            Map mapHelp2 = new HashMap();
            mapHelp1.put(route.getStartDestination().getName(), route.getEndDestination().getName());
            mapHelp2.put(route.getEndDestination().getName(), route.getStartDestination().getName());
            if (!mapList.contains(mapHelp1) && !mapList.contains(mapHelp2)){
                mapList.add(mapHelp1);
                Route helpRoute = new Route();
                helpRoute.setStartDestination(new City(route.getStartDestination().getName(), route.getStartDestination().getCountry()));
                helpRoute.setEndDestination(new City(route.getEndDestination().getName(), route.getEndDestination().getCountry()));
                routeList.add(helpRoute);
            }
        }
        return routeList;
    }

    @Override
    public int getPriceForRoute(Route route, LocalDate dateFrom, LocalDate dateTo) throws ServiceException {
        try {
            return bookingDAO.getPriceForRoute(route, dateFrom, dateTo);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public int getNumberOfBookedSeats(LocalDate date) throws ServiceException {
        try {
            return bookingDAO.getNumberOfBookedSeats(date);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
