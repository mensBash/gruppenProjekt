package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;

import java.util.Objects;

public class BookingEntry{
    private Customer customer;
    private Integer seatNo;
    private Route route;

    public BookingEntry(Customer customer, Integer seatNo, Route route) {
        this.customer = customer;
        this.seatNo = seatNo;
        this.route = route;
    }

    public BookingEntry() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(Integer seatNo) {
        this.seatNo = seatNo;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }


    @Override
    public String toString() {
        return "BookingEntry{" +
            "customer=" + customer +
            ", seatNo=" + seatNo +
            ", route=" + route +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingEntry)) return false;
        BookingEntry that = (BookingEntry) o;
        return Objects.equals(getCustomer(), that.getCustomer()) &&
            Objects.equals(getSeatNo(), that.getSeatNo()) &&
            Objects.equals(getRoute(), that.getRoute());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCustomer(), getSeatNo(), getRoute());
    }

}

