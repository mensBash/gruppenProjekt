package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;

public class TicketCounter implements Comparable<TicketCounter> {
    private String departureCity;
    private String arrivalCity;
    private int oneWayTicketsCounter;
    private int returnTicketsCounter;

    public TicketCounter(String departureCity, String arrivalCity, int oneWayTicketsCounter, int returnTicketsCounter) {
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.oneWayTicketsCounter = oneWayTicketsCounter;
        this.returnTicketsCounter = returnTicketsCounter;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public int getOneWayTicketsCounter() {
        return oneWayTicketsCounter;
    }

    public void setOneWayTicketsCounter(int oneWayTicketsCounter) {
        this.oneWayTicketsCounter = oneWayTicketsCounter;
    }

    public int getReturnTicketsCounter() {
        return returnTicketsCounter;
    }

    public void setReturnTicketsCounter(int returnTicketsCounter) {
        this.returnTicketsCounter = returnTicketsCounter;
    }



    @Override
    public int compareTo(TicketCounter o) {
        return (o.getReturnTicketsCounter() + o.getOneWayTicketsCounter()) - (this.getOneWayTicketsCounter() + this.getReturnTicketsCounter());
    }
}
