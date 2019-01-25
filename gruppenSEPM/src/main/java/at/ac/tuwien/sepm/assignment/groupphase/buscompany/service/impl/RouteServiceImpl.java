package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dao.RouteDAO;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.Graph;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.validator.ServiceValidator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RouteServiceImpl implements RouteService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final int MAX_ROUTES = 10;
    private final long MAX_TRANSFER_HOURS = 5;
    private final long MIN_TRANSFER_MINUTES = 15;


    private RouteDAO routeDAO;

    private Graph graph;


    private ServiceValidator serviceValidator;

   @Autowired
    private BookingService bookingService;

    @Autowired
    public RouteServiceImpl(ServiceValidator serviceValidator,RouteDAO routeDAO,Graph graph)
    {

        this.graph=graph;
        this.routeDAO=routeDAO;
        this.serviceValidator = serviceValidator;
    }

    @Override
    public void addRoute(Route r) throws ServiceException,InvalidRouteException {
        try {
            serviceValidator.validateServiceRoute(r);

            if(!graph.getGraph().containsVertex(r.getStartDestination())){
                r.getStartDestination().setName(r.getStartDestination().getName().toLowerCase());
                routeDAO.createCities(r,true);
                graph.getGraph().addVertex(r.getStartDestination());
            }
            if(!graph.getGraph().containsVertex(r.getEndDestination())){
                r.getEndDestination().setName(r.getEndDestination().getName().toLowerCase());
                routeDAO.createCities(r,false);
                graph.getGraph().addVertex(r.getEndDestination());
            }
            if(graph.getGraph().containsEdge(r)){
                throw new ServiceException("Route already exists!");
            }

            routeDAO.createRoute(r);
            graph.getGraph().addEdge(r.getStartDestination(),r.getEndDestination(),r);
            graph.getGraph().setEdgeWeight(r,r.getPrice());

        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }
    @Override
    public void updateRoute(Route route) throws ServiceException,InvalidRouteException{
        try {
            serviceValidator.validateServiceRoute(route);
            routeDAO.updateRoute(route);
            graph.createNewGraph();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void updateTimetable(Route route, Map<Timetable, Double> timetableList) throws ServiceException{
        try {
            routeDAO.updateTimetable(route, timetableList);
            graph.createNewGraph();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public void checkOverlap(Timetable timetable, Collection<Timetable> timetableList) throws ServiceException {
        try {
            serviceValidator.checkOverlap(timetable, timetableList);
        } catch (ServiceValidatorException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public List<Integer> loadSeats(Route route) throws ServiceException {
        List<Integer> seatsStatus;
        try {
            if(route==null || route.getRid()==null) {
                throw new ServiceException("Route is null");
            }else{
                seatsStatus = routeDAO.loadSeats(route);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return seatsStatus;
    }

    @Override
    public void initializeGraph() throws ServiceException {
        try {
            List<Route> routes = routeDAO.initializeGraph();
            for(Route r : routes){
                if(!graph.getGraph().containsVertex(r.getStartDestination())){
                    graph.getGraph().addVertex(r.getStartDestination());
                }
                if(!graph.getGraph().containsVertex(r.getEndDestination())){
                    graph.getGraph().addVertex(r.getEndDestination());
                }
                graph.getGraph().addEdge(r.getStartDestination(),r.getEndDestination(),r);
                graph.getGraph().setEdgeWeight(r,r.getPrice());
            }
            List<City> cities = routeDAO.takeCitiesToGraph();
            for(City c : cities){
                if(!graph.getGraph().containsVertex(c)){
                    graph.getGraph().addVertex(c);
                }
            }

        } catch (PersistenceException e) {
            throw new ServiceException("Error by initializing graph!",e);
        }
    }

    @Override
    public Map<Timetable,Double> loadTimetable(Route route) throws ServiceException {
        Map<Timetable,Double> timetableList;
        if(route==null){
            throw new ServiceException("Route is null");
        }
        try{
            timetableList = routeDAO.loadTimetable(route);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return timetableList;
    }

    @Override
    public double calculatePrice(List<Route> routes) throws ServiceException {
        if(routes==null){
            throw new ServiceException("Invalid parameter!");
        }
        double sum=0;
        for(Route r : routes){
            sum += r.getPrice();
        }
        return sum;
    }

    @Override
    public double calculateDistance(List<Route> routes) throws ServiceException {
        if(routes==null){
            throw new ServiceException("Invalid parameter!");
        }
        double sum=0;
        for(Route r : routes){
            sum+= r.getDistance();
        }
        return sum;
    }

    @Override
    public List<Long> isBooked(Timetable timetable) throws ServiceException {
        List<Long> book =new ArrayList<>();
        for(Booking b : bookingService.loadBookings()){
            for(BookingEntry e : b.getEntries()){
                if(e.getRoute().getRid().equals(timetable.getId())){
                    if(!book.contains(b.getBookingNr())){
                        book.add(b.getBookingNr());
                    }

                }
            }
        }
        return book;
    }

    @Override
    public List<Route> getAllSimpleRoutes() throws ServiceException {
        List<Route> routes = new ArrayList<>();
        for(Route r : graph.getGraph().edgeSet()){
            boolean temp =true;
            for(Route oldRoute : routes){
                if(oldRoute.getStartDestination().equals(r.getStartDestination())
                    && oldRoute.getEndDestination().equals(r.getEndDestination())){
                    temp =false;
                    break;
                }
            }
            if(temp){
                routes.add(r);
            }
        }
        return routes;
    }

    @Override
    public List<City> getAllCities() throws ServiceException {
        List<City> cities;
        try {
            cities = routeDAO.getAllCities();
        } catch (PersistenceException e) {
            throw new ServiceException("Cities cannot be restored from DB",e);
        }
        return cities;
    }

    @Override
    public Map<FareItem, Route> findFares(City city, City city1, LocalDate departure) throws ServiceException,NotFoundException {

      Route r = new Route();
       r.setEndDestination(city1);
       r.setStartDestination(city);
       r.setDepartureDate((departure==null)?LocalDate.now():departure);
       r.setWithTransfer(true);
        if(city1==null){
            return allFaresFrom(r,r.getDepartureDate());
        }
        List<List<Route>> paths =searchRoutes(r,false, "cheapest",false);
        if(paths.isEmpty()){
            throw new NotFoundException("Sorry there aren't any routes that match your criteria!");
        }
        Map<FareItem,Route> map = new HashMap<>();

        for(List<Route> path :paths){
            Route newRoute = new Route();
            newRoute.setStartDestination(city);
            newRoute.setEndDestination(city1);
            newRoute.setPrice(calculatePrice(path));
            newRoute.setDepartureDate(path.get(0).getTimetable().getDepartureTime().toLocalDate());
            if(Math.abs( ChronoUnit.MONTHS.between(r.getDepartureDate(),newRoute.getDepartureDate())) <6){
                map.putIfAbsent(new FareItem(newRoute.getDepartureDate().getMonth(),newRoute.getEndDestination()),newRoute);
            }

        }

        if(map.isEmpty()){
            throw new NotFoundException("Sorry there aren't any routes that match your criteria!");
        }


        return sortMap(map);
    }

    @Override
    public Map<LocalDate, Route> getRoutesForMonth(Route r) throws ServiceException {

        Map<LocalDate,Route> res = new HashMap<>();
        r.setDepartureDate(LocalDate.of(r.getDepartureDate().getYear(),r.getDepartureDate().getMonth(),1));
        r.setWithTransfer(true);
        List<List<Route>> paths = searchRoutes(r,false, "default",false);

        for(List<Route> path:paths){
            if(path.get(0).getTimetable().getDepartureTime().getMonth() == r.getDepartureDate().getMonth()){
                Route newRoute = new Route();
                newRoute.setStartDestination(r.getStartDestination());
                newRoute.setEndDestination(r.getEndDestination());
                newRoute.setPrice(calculatePrice(path));
                newRoute.setWithTransfer(true);
                newRoute.setDepartureDate(path.get(0).getTimetable().getDepartureTime().toLocalDate());
                res.putIfAbsent(newRoute.getDepartureDate(),newRoute);
            }

        }

        YearMonth yearMonthObject = YearMonth.of(r.getDepartureDate().getYear(),r.getDepartureDate().getMonth());
        int max = yearMonthObject.lengthOfMonth();
        for(int i = 1 ; i <= max ; i++){
            Route newRoute=new Route(r.getStartDestination(),r.getEndDestination(),"",0.0,53,0.0);
            newRoute.setDepartureDate(LocalDate.of(r.getDepartureDate().getYear(),r.getDepartureDate().getMonth(),i));
            res.putIfAbsent(newRoute.getDepartureDate(),newRoute);
        }

        return new TreeMap<>(res);
    }

    @Override
    public List<List<Route>> searchRoutes(Route searchRoute, boolean returnTicket, String sort,boolean limit) throws ServiceException {
        // validate search parameters that are typed/chosen
        validateSearchParameters(searchRoute);

        City startDest = searchRoute.getStartDestination();
        City endDest = searchRoute.getEndDestination();
        //LOG.debug("startdest: " + startDest + " enddest: " + endDest);
        LocalDate depDate = searchRoute.getDepartureDate();
        if (returnTicket) {
            startDest = searchRoute.getEndDestination();
            endDest = searchRoute.getStartDestination();
            depDate = searchRoute.getReturnDate();
        }
        AllDirectedPaths<City, Route> paths = new AllDirectedPaths<>(graph.getGraph());
        int maxPathLength = searchRoute.getWithTransfer() ? 3 : 2;

        List<GraphPath<City, Route>> all;
        try {
            all = paths.getAllPaths(startDest, endDest, true, maxPathLength);
        } catch (Exception e) {
            return new ArrayList<>();
        }
        List<List<Route>> allRoutes = new ArrayList<>();
        for (GraphPath<City, Route> p : all) {
            allRoutes.add(p.getEdgeList());
        }
        if (!searchRoute.getWithTransfer()) {
            allRoutes.removeIf(i -> i.size() > 1);
        }

        LocalDate chosenDate = depDate;
        List<List<Route>> result = new ArrayList<>();
        int count = 0;
        for (List<Route> route : allRoutes) {
            //direct route(without change)
            if (route.size() == 1) {
                if (isValidDate(route.get(0), chosenDate)) {
                    if (!chosenDate.isEqual(route.get(0).getTimetable().getDepartureTime().toLocalDate())) {
                        if(limit)  count++;
                    }
                    if (count <= MAX_ROUTES) {
                        result.add(route);
                    }
                }
            }
            // routes with change
            else {
                boolean temp = true;
                if (!isValidDate(route.get(0), chosenDate)) {
                    temp = false;
                } else {
                    for (int i = 0; i < route.size() - 1; i++) {
                        if (route.get(i + 1).getTimetable().getDepartureTime().isBefore(route.get(i).getTimetable().getArrivalTime()) ||
                            Math.abs(ChronoUnit.HOURS.between(route.get(i + 1).getTimetable().getDepartureTime(), route.get(i).getTimetable().getArrivalTime())) > MAX_TRANSFER_HOURS ||
                            Math.abs(ChronoUnit.MINUTES.between(route.get(i + 1).getTimetable().getDepartureTime(), route.get(i).getTimetable().getArrivalTime())) < MIN_TRANSFER_MINUTES) {
                            temp = false;
                            break;
                        }
                    }
                }
                if (temp && count < MAX_ROUTES) {
                    if (!chosenDate.isEqual(route.get(0).getTimetable().getDepartureTime().toLocalDate())) {
                        if(limit) count++;
                    }
                    result.add(route);
                }
            }
        }

        if (sort.equals("default")){
            return sortDefault(result);
        }else if (sort.equals("cheapest")){
            return sortCheapest(result);
        }else if (sort.equals("fastest")){
            return sortFastest(result);
        }else if (sort.equals("shortest")){
            return sortShortest(result);
        }
        return sortDefault(result);
    }

    @Override
    public List<Route> filterSimpleRoutes(Route filter) throws ServiceException {
        List<Route> routes = getAllSimpleRoutes();
        if(filter==null){
            throw new ServiceException("Invalid parameters");
        }
        LOG.debug("SERVICE: Filtering list of simple routes!");
        LOG.debug("First: "+filter.getStartDestination() +" Second: "+filter.getEndDestination());
        List<Route> result = new ArrayList<>();
        if(filter.getStartDestination()!=null && filter.getEndDestination()!=null){

            for(Route r : routes){
                if(r.getStartDestination().equals(filter.getStartDestination()) &&
                    r.getEndDestination().equals(filter.getEndDestination())){
                    result.add(r);
                }
            }
        }else if (filter.getStartDestination()!=null){

            for(Route r : routes){
                if(r.getStartDestination().equals(filter.getStartDestination())){
                    result.add(r);
                }
            }
        }else if (filter.getEndDestination()!=null){

            for(Route r : routes){
                if(r.getEndDestination().equals(filter.getEndDestination())){
                    result.add(r);
                }
            }
        }
        return result;

    }


    @Override
    public List<String> getAllCitiesNames() throws ServiceException {
        List<String> cities;
        try {
            cities = routeDAO.getAllCitiesNames();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return cities;
    }

    @Override
    public List<String> getAllCountriesName() throws ServiceException {
        List<String> countries;
        try {
            countries = routeDAO.getAllCountriesName();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return countries;
    }

    @Override
    public List<String> getAllBuses() throws ServiceException {
        List<String> buses;
        try {
            buses = routeDAO.getAllBuses();
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }
        return buses;
    }

    private Map<FareItem,Route> sortMap(Map<FareItem,Route> map){
        List<Map.Entry<FareItem, Route>> list = new ArrayList<>(map.entrySet());
        list.sort((val1,val2)->(int) ( val1.getValue().getPrice()-val2.getValue().getPrice()));
        Map<FareItem,Route> result = new LinkedHashMap<>();
        for (Map.Entry<FareItem,Route> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private Map<FareItem, Route>  allFaresFrom(Route searchRoute,LocalDate departure) throws ServiceException, NotFoundException {
        List<City> cities = getAllCities();
        cities.remove(searchRoute.getStartDestination());
        Map<FareItem,Route> map = new HashMap<>();
        for(City c : cities){
            searchRoute.setEndDestination(c);
            List<List<Route>> paths =searchRoutes(searchRoute,false,"cheapest",false);
            if(!paths.isEmpty()){

                //for(List<Route> path :paths){
                    Route newRoute = new Route();
                    newRoute.setStartDestination(searchRoute.getStartDestination());
                    newRoute.setEndDestination(searchRoute.getEndDestination());
                    newRoute.setPrice(calculatePrice(paths.get(0)));
                    newRoute.setDepartureDate(paths.get(0).get(0).getTimetable().getDepartureTime().toLocalDate());
                    if(Math.abs( ChronoUnit.MONTHS.between(departure,newRoute.getDepartureDate())) <6){
                        map.putIfAbsent(new FareItem(newRoute.getDepartureDate().getMonth(),newRoute.getEndDestination()),newRoute);
                    }

                //}


            }
        }

        if(map.isEmpty()){
            throw new NotFoundException("Sorry there aren't any routes that match your criteria!");
        }


        return sortMap(map);
    }

    private boolean isValidDate(Route route, LocalDate chosenDate) {
        return route.getTimetable().getDepartureTime().isAfter(LocalDateTime.of(chosenDate, LocalTime.MIN));
    }

    private void validateSearchParameters(Route searchRoute) throws ServiceException {
        try {
           List<String>  cities = routeDAO.getAllCitiesNames();
            boolean startCity = false;
            boolean endCity = false;


            for (String city : cities) {
                if (city.equalsIgnoreCase(searchRoute.getStartDestination().getName())) {
                    startCity = true;
                    break;
                }
            }
            for (String city : cities) {
                if (city.equalsIgnoreCase(searchRoute.getEndDestination().getName())) {
                    endCity = true;
                    break;
                }
            }
            if (!endCity || !startCity) {
                throw new ServiceException("Invalid input for destinations. Please choose offered destinations!");
            }
            //validate dates for search
            if (searchRoute.getIncludeReturn()) {
                if (searchRoute.getReturnDate() != null) {
                    if (searchRoute.getReturnDate().isBefore(searchRoute.getDepartureDate())) {
                        throw new ServiceException("Return date cannot be before departure date!");
                    }
                }else {
                    throw new ServiceException("Return date is not specified!");
                }
            }

            if(searchRoute.getStartDestination().equals(searchRoute.getEndDestination())){
                throw new ServiceException("Cities can not be equal!");
            }

        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(),e);
        }


    }
    private List<List<Route>> sortDefault(List<List<Route>> result) {
        result.sort((o1, o2) -> {
                if (o1.size() != o2.size()) {
                    if (o1.isEmpty() || o2.isEmpty()) {
                        return 0;
                    } else {
                        return o1.get(0).getTimetable().getDepartureTime().compareTo(o2.get(0).getTimetable().getDepartureTime());
                    }
                }
                for (int i = 0; i < o1.size(); i++) {
                    int b = o1.get(i).getTimetable().getDepartureTime().compareTo(o2.get(i).getTimetable().getDepartureTime());
                    if (b != 0) {
                        return b;
                    }
                }
            return 0;
        });
        return result;
    }

    private List<List<Route>> sortCheapest(List<List<Route>> result){
        result.sort((o1, o2) -> {
            Double price1 = 0.0;
            Double price2 = 0.0;
            if (o1.size() == o2.size()) {
                for (int i = 0; i < o1.size(); i++) {
                    price1 += o1.get(i).getPrice();
                    price2 += o2.get(i).getPrice();
                }
            } else {
                for (int i = 0; i < o1.size(); i++) {
                    price1 += o1.get(i).getPrice();
                }
                for (int i = 0; i < o2.size(); i++) {
                    price2 += o2.get(i).getPrice();
                }
            }
            int b = price1.compareTo(price2);
            if (b != 0) {
                return b;
            }
            return 0;
        });
        return result;
    }

    private List<List<Route>> sortShortest(List<List<Route>> result){
        result.sort((o1, o2) -> {
                Double distance1 = 0.0;
                Double distance2 = 0.0;
                if (o1.size() == o2.size()) {
                    for (int i = 0; i < o1.size(); i++) {
                        distance1 += o1.get(i).getDistance();
                        distance2 += o2.get(i).getDistance();
                    }
                } else {
                    for (int i = 0; i < o1.size(); i++) {
                        distance1 += o1.get(i).getDistance();
                    }
                    for (int i = 0; i < o2.size(); i++) {
                        distance2 += o2.get(i).getDistance();
                    }
                }
                int b = distance1.compareTo(distance2);
                if (b != 0) {
                    return b;
                }
                return 0;
        });
        return result;
    }

    private List<List<Route>> sortFastest(List<List<Route>> result){
        result.sort((o1, o2) -> {
                Long days1 = ChronoUnit.HOURS.between(o1.get(0).getTimetable().getDepartureTime(), o1.get(o1.size() - 1).getTimetable().getArrivalTime());
                Long days2 = ChronoUnit.HOURS.between(o2.get(0).getTimetable().getDepartureTime(), o2.get(o2.size() - 1).getTimetable().getArrivalTime());
                int b = days1.compareTo(days2);
                if (b != 0) {
                    return b;
                }
                return 0;
        });
        return result;
    }
}
