package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Booking {

    private Long bid;
    private List<BookingEntry> entries;
    private Status status;
    private Double price;
    private Long bookingNr;
    private LocalDateTime timeOfBooking;

    public enum Status {
        COMPLETED, CANCELLED
    }


    public Booking(){}

    public Booking(Long bid, Status status, Double price) {
        this.bid = bid;
        this.entries = new ArrayList<>();
        this.status = status;
        this.price = price;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<BookingEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<BookingEntry> entries) {
        this.entries = entries;
    }

    public Long getBookingNr() {
        return bookingNr;
    }

    public void setBookingNr(Long bookingNr) {
        this.bookingNr = bookingNr;
    }

    public LocalDateTime getTimeOfBooking() {
        return timeOfBooking;
    }

    public void setTimeOfBooking(LocalDateTime timeOfBooking) {
        this.timeOfBooking = timeOfBooking;
    }

    @Override
    public String toString() {
        return "Booking{" +
            "bid=" + bid +
            ", entries=" + entries +
            ", status=" + status +
            ", price=" + price +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bid, booking.bid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(bid);
    }
}
