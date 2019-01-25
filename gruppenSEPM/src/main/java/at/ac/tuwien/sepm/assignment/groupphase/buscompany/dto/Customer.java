package at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto;

import java.time.LocalDate;
import java.util.Objects;

public class Customer {

    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthday;

    private String socialNumber;


    public Customer() {
    }

    public Customer(String firstName, String lastName, String socialNumber, String email, LocalDate birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialNumber = socialNumber;
        this.email = email;
        this.birthday = birthday;
    }


    public String getSocialNumber() {
        return socialNumber;
    }

    public void setSocialNumber(String socialNumber) {
        this.socialNumber = socialNumber;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(getFirstName(), customer.getFirstName()) &&
            Objects.equals(getLastName(), customer.getLastName()) &&
            Objects.equals(getSocialNumber(), customer.getSocialNumber());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getFirstName(), getLastName(), getSocialNumber());
    }
}
