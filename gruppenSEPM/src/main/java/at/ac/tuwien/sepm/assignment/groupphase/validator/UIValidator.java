package at.ac.tuwien.sepm.assignment.groupphase.validator;

import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidInputException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class UIValidator {

    private final String regex = "^[a-zA-Z0-9_ ]*$";
    private final String regexLetters = "^[a-zA-Z_ ]*$";
    private final String regexHours ="([01]?[0-9]|2[0-3])";
    private final String regexMin = "[0-5][0-9]";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    public Route validateUIRoute(String bus_nr, String start_destination_city, String end_destination_city, String start_destination_country, String end_destination_country, String price, String distance) throws InvalidInputException {
        LOG.info("Validating UI input");
        String warning = "";
        Integer capacity_temp = 53;
        if(bus_nr==null || !bus_nr.matches(regex) || bus_nr.trim().isEmpty()){
            warning+="Invalid route number\n";
        }
        if(start_destination_city==null || !start_destination_city.matches(regexLetters) || start_destination_city.trim().isEmpty()){
            warning+="Invalid start destination city\n";
        }
        if(end_destination_city==null || !end_destination_city.matches(regexLetters) || end_destination_city.trim().isEmpty()){
            warning+="Invalid end destination city\n";
        }
        if(start_destination_country==null || !start_destination_country.matches(regexLetters) || start_destination_country.trim().isEmpty()){
            warning+="Invalid start destination country\n";
        }
        if(end_destination_country==null || !end_destination_country.matches(regexLetters) || end_destination_country.trim().isEmpty()){
            warning+="Invalid end destination country\n";
        }
        try{
            Double.parseDouble(price);
        }catch (NumberFormatException | NullPointerException e){
            warning+="Invalid price\n";
        }
        try{
           Double.parseDouble(distance);
        }catch(NumberFormatException  | NullPointerException e){
            LOG.error(e.getMessage());
            warning+="Invalid distance\n";
        }

        if(!warning.isEmpty()){
            throw new InvalidInputException(warning);
        }
        City from = new City(start_destination_city, start_destination_country);
        City to = new City(end_destination_city, end_destination_country);
        Route route = new Route(from, to, bus_nr, Double.parseDouble(price), capacity_temp, Double.parseDouble(distance));
        //route.getTimetable().add(new Timetable(departure, arrival));
       // ArrayList<Map.Entry<LocalDateTime,LocalDateTime>> list = route.getList();
        LOG.info("UI Validation successfully passed.");
        return route;
    }

    public double validatePrice(String priceText) throws InvalidInputException {
        String regexDouble = "^?([0-9]+(?:[.][0-9]*)?|\\.[0-9]+)$";
        Double price;
        try{
            if(!priceText.matches(regexDouble)){
                throw new InvalidInputException("Price is invalid!");
            }
            price = Double.parseDouble(priceText);
        }catch (NullPointerException e){
            throw new InvalidInputException("Price is invalid");
        }
        return price;
    }
    public Timetable validateTime(LocalDate departure_date, LocalDate arrival_date, String departure_hours, String arrival_hours, String departure_min, String arrival_min) throws InvalidInputException {
        boolean date_time_check = false;
        String warning = "";
        LocalDateTime departure = null;
        LocalDateTime arrival = null;
        if(departure_date==null){
            warning+="Invalid departure date\n";
            date_time_check = true;
        }
        if(arrival_date==null){
            warning+="Invalid arrival date\n";
            date_time_check = true;
        }
        if(departure_hours==null || !departure_hours.matches(regexHours) || departure_hours.trim().isEmpty()){
            warning+="Invalid departure hours\n";
            date_time_check = true;
        }
        if(arrival_hours==null || !arrival_hours.matches(regexHours) || arrival_hours.trim().isEmpty()){
            warning+="Invalid arrival hours\n";
            date_time_check = true;
        }
        if(departure_min==null || !departure_min.matches(regexMin) || departure_min.trim().isEmpty()){
            warning+="Invalid departure minutes\n";
            date_time_check = true;
        }
        if(arrival_min==null || !arrival_min.matches(regexMin) || arrival_min.trim().isEmpty()){
            warning+="Invalid arrival minutes\n";
            date_time_check = true;
        }
        if (!date_time_check){
            departure = LocalDateTime.of(departure_date, LocalTime.of(Integer.parseInt(departure_hours), Integer.parseInt(departure_min)));
            arrival = LocalDateTime.of(arrival_date, LocalTime.of(Integer.parseInt(arrival_hours), Integer.parseInt(arrival_min)));
            if(!departure.isBefore(arrival)){
                warning+="Arrival date must be after departure date\n";
            }else if(departure.isBefore(LocalDateTime.now())){
                warning+="Departure invalid";
            }
        }
        if(!warning.isEmpty()){
            throw new InvalidInputException(warning);

        }
            return new Timetable(departure, arrival);
    }

    public void validateCustomer(String name, String lastName, String email, String socialNumber, LocalDate birthday) throws CustomerException {
        String warning = "";
        if(name == null ||  lastName == null || lastName.isEmpty() || name.isEmpty() || !name.matches(regexLetters) || !lastName.matches(regexLetters)){
            warning += "Invalid name/last name, reconsider your input.\n";
        }
        if(socialNumber == null || socialNumber.isEmpty() || !socialNumber.matches("^[a-zA-Z0-9_]*$")){
            warning += "Invalid social number, reconsider your input.\n";
        }
        if(birthday == null){
            warning += "Please provide date of birth\n";
        }
        else if(birthday.isAfter(LocalDate.now()) || birthday.isAfter(LocalDate.now().minusYears(14))){
            warning += "Either too young person or entered date is in future.\n";
        }
        if(!EmailValidator.getInstance().isValid(email)){
            warning += "Please enter valid e-mail address";
        }
        if(!warning.isEmpty()){
            throw new CustomerException(warning);
        }

    }
}
