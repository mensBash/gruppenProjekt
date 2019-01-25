package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;

import java.time.Month;
import java.util.Objects;

public class FareItem {
    private Month month;
    private City city;

    public FareItem(Month month, City city) {
        this.month = month;
        this.city = city;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FareItem fareItem = (FareItem) o;
        return month == fareItem.month &&
            Objects.equals(city, fareItem.city);
    }

    @Override
    public int hashCode() {

        return Objects.hash(month, city);
    }
}
