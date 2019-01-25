package at.ac.tuwien.sepm.assignment.groupphase.validator;

import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidRouteException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceValidatorException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class ServiceValidator {


    private final String regex = "^[a-zA-Z0-9_ ]*$";
    private final String regexLetters = "^[a-zA-Z_ ]*$";
    private final String regexTime ="([01]?[0-9]|2[0-3]):[0-5][0-9]";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public void validateServiceRoute(Route r) throws  InvalidRouteException {
        String warning = "";
        LOG.info("Validating route");

        if(r.getBusNumber()==null || !r.getBusNumber().matches(regex) || r.getBusNumber().trim().isEmpty()){
            warning+="Invalid route number\n";
        }

        if(r.getStartDestination()==null || !r.getStartDestination().getName().matches(regexLetters) || r.getStartDestination().getName().trim().isEmpty()) {
            warning += "Invalid start destination\n";
        }
        if(r.getEndDestination()==null ||!r.getEndDestination().getName().matches(regexLetters) ||  r.getEndDestination().getName().trim().isEmpty()){
            warning+="Invalid end destination\n";
        }
        if(r.getPrice()==null || r.getPrice()<=0 || r.getPrice() > 10000) {
            warning += "Invalid price\n";
        }
        if(r.getDistance()==null || r.getDistance()<=0){

            warning+="Invalid distance\n";
        }
        if(r.getCapacity()== null || r.getCapacity()<=0){
            warning+="Invalid capacity\n";
        }
        if(r.getStartDestination()!=null && r.getEndDestination()!=null &&r.getStartDestination().equals(r.getEndDestination())){
            warning+="Start and end destination are same\n";
        }
        boolean date_time_check = false;
        if(r.getTimetable() != null){

                if (r.getTimetable().getDepartureTime() == null) {
                    warning += "Invalid departure date\n";
                    date_time_check = true;
                }
                if (r.getTimetable().getArrivalTime() == null) {
                    warning += "Invalid arrival date\n";
                    date_time_check = true;
                }
                if (!date_time_check) {
                    if (!r.getTimetable().getDepartureTime().isBefore(r.getTimetable().getArrivalTime())) {
                        warning += "Arrival date must be after departure date\n";
                    } else if (r.getTimetable().getDepartureTime().isBefore(LocalDateTime.now())) {
                        warning += "Departure invalid";
                    }
                }


        }
        if(!warning.isEmpty()){
            throw new InvalidRouteException(warning);
        }else {
            LOG.info("Route validation successfully passed.");
        }
    }

    public void checkOverlap(Timetable timetable, Collection<Timetable> timetableList) throws ServiceValidatorException{
        for (Timetable t : timetableList) {
            if (t.getDepartureTime().isBefore(timetable.getArrivalTime()) && t.getArrivalTime().isAfter(timetable.getDepartureTime())){
                throw new ServiceValidatorException("Time periods cannot overlap");
            }
        }
    }
}
