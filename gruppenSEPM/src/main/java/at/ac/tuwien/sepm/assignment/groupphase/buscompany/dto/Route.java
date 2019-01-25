package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.time.LocalDate;
import java.util.*;

public class Route extends DefaultWeightedEdge {

    private Long rid;
    private String busNumber;
    private Double price;
    private Timetable timetable;
    private Integer capacity;
    private City startDestination;
    private City endDestination;
    private Double distance;
    private Double blindBookingPrice;
    // this below is for searching routes
    private LocalDate departureDate;
    private LocalDate returnDate;
    private Boolean includeReturn;
    private Boolean withTransfer;


    public Route(){
        super();
        includeReturn=false;
        withTransfer=false;
    }
    public Route(City start, City end, String busNumber, Double price, Integer capacity, Double distance) {
        this();
        this.startDestination=start;
        this.endDestination=end;
        this.busNumber = busNumber;
        this.price = price;
        this.capacity = capacity;
        this.distance=distance;
    }

    public Route(Route originRoute){
        this.rid = originRoute.getRid();
        this.timetable = originRoute.getTimetable();
        this.startDestination=originRoute.getStartDestination();
        this.endDestination=originRoute.getEndDestination();
        this.busNumber = originRoute.getBusNumber();
        this.price = originRoute.getPrice();
        this.capacity = originRoute.getCapacity();
        this.distance=originRoute.getDistance();
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDistance(){
        return this.distance;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "Route{" +
            "rid=" + rid +
            ", busNumber='" + busNumber + '\'' +
            ", price=" + price +
            ", timetable=" + timetable +
            ", capacity=" + capacity +
            ", startDestination=" + startDestination +
            ", endDestination=" + endDestination +
            ", distance=" + distance +
            '}';
    }

    public City getStartDestination() {
        return startDestination;
    }

    public void setStartDestination(City startDestination) {
        this.startDestination = startDestination;
    }

    public City getEndDestination() {
        return endDestination;
    }

    public void setEndDestination(City endDestination) {
        this.endDestination = endDestination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(rid, route.rid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startDestination, endDestination, timetable);
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }


    public Boolean getIncludeReturn() {
        return includeReturn;
    }

    public void setIncludeReturn(Boolean includeReturn) {
        this.includeReturn = includeReturn;
    }

    public Boolean getWithTransfer() {
        return withTransfer;
    }

    public void setWithTransfer(Boolean withTransfer) {
        this.withTransfer = withTransfer;
    }

    public Double getBlindBookingPrice() {
        return blindBookingPrice;
    }

    public void setBlindBookingPrice(Double blindBookingPrice) {
        this.blindBookingPrice = blindBookingPrice;
    }
}
