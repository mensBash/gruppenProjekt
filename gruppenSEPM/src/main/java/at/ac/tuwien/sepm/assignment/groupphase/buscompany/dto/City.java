package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;

import java.util.Objects;

public class City {

    private String name;
    private String country;
    private boolean blindBooking;
    private String picture;

    public City(String name,String country) {
        this.country=country;
        if(name!=null) {
            this.name = name;
        }
    }

    public City(){}

    public String getPicture(){
        return picture;
    }

    public void setPicture(String pic){
        this.picture = pic;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
       if(o instanceof City){
           City city = (City)o;
           return name.equalsIgnoreCase(city.name);
       }else{
           return false;
       }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        /*return "City{" +
            "name='" + name + '\'' +
            ", country='" + country + '\'' +
            '}';*/
        return name;
    }

    public boolean isBlindBooking() {
        return blindBooking;
    }

    public void setBlindBooking(boolean blindBooking) {
        this.blindBooking = blindBooking;
    }
}
