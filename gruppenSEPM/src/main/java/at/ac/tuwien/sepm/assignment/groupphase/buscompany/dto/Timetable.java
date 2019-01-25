package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;


import java.time.LocalDateTime;
import java.util.Objects;

public class Timetable {
    private Long id;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    public Timetable(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timetable timetable = (Timetable) o;
        return Objects.equals(departureTime, timetable.departureTime) &&
            Objects.equals(arrivalTime, timetable.arrivalTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(departureTime, arrivalTime);
    }

    @Override
    public String toString() {
        return "Timetable{" +
            "departureTime=" + departureTime +
            ", arrivalTime=" + arrivalTime +
            '}';
    }
}
