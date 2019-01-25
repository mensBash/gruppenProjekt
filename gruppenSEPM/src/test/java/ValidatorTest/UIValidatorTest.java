package ValidatorTest;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidInputException;
import at.ac.tuwien.sepm.assignment.groupphase.validator.UIValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class UIValidatorTest {

    private String bus;
    private String start;
    private String end;
    private String startCountry;
    private String endCountry;
    private String price;
    private String distance;

    LocalDate depDate;
    LocalDate arrDate;

    private UIValidator uiValidator = new UIValidator();


    @Before
    public void setUp(){
        bus = null;
        start = null;
        end = null;
        startCountry = null;
        endCountry = null;
        price = null;
        distance = null;
        depDate = LocalDate.of(2018, 8, 10);
        arrDate = LocalDate.of(2018,8, 11);
    }

    @Test(expected = InvalidInputException.class)
    public void testBusIsNull() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute(bus, "city", "town", "austria","spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testStarDestIsNull() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", start, "town", "austria","spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testEndDestIsNull() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", end, "austria","spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testStartCountryIsNull() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", startCountry,"spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testEndCountryIsNull() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "austria",endCountry, "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testPriceIsNull() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "austria","spain", price, "330.0");
    }
    @Test(expected = InvalidInputException.class)
    public void testDistanceIsNull() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "austria","spain", "34.0", null);
    }

    @Test(expected = InvalidInputException.class)
    public void testBusIsEmpty() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("", "city", "town", "austria","spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testStartIsEmpty() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "", "town", "austria","spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testEndIsEmpty() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "", "austria","spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testStartCountryIsEmpty() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "","spain", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testEndCountryIsEmpty() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "austria","", "34.0", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testPriceIsEmpty() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "austria","spain", "", "330.0");
    }

    @Test(expected = InvalidInputException.class)
    public void testDistanceIsEmpty() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "austria","spain", "34.0", "");
    }

    @Test
    public void testValidData() throws InvalidInputException {
        Route r = uiValidator.validateUIRoute("bus", "city", "town", "austria","spain", "34.0", "330.0");
        Assert.assertNotNull(r);
    }



    @Test (expected = InvalidInputException.class)
    public void testDepartureTimeIsNull() throws InvalidInputException {
        depDate = null;
        Timetable t = uiValidator.validateTime(depDate, arrDate, "10", "12", "00", "00");
    }

    @Test (expected = InvalidInputException.class)
    public void testArrivalTimeIsNull() throws InvalidInputException {
        arrDate = null;
        Timetable t = uiValidator.validateTime(depDate, arrDate, "10", "12", "00", "00");
    }

    @Test (expected = InvalidInputException.class)
    public void testDepartureHoursIsNull() throws InvalidInputException {
        Timetable t = uiValidator.validateTime(depDate, arrDate, null, "12", "00", "00");
    }

    @Test (expected = InvalidInputException.class)
    public void testArrivalHoursIsNull() throws InvalidInputException {
        Timetable t = uiValidator.validateTime(depDate, arrDate, "10", null, "00", "00");
    }

    @Test (expected = InvalidInputException.class)
    public void testDepartureMinutesAreNull() throws InvalidInputException {
        Timetable t = uiValidator.validateTime(depDate, arrDate, "10", "12", null, "00");
    }

    @Test (expected = InvalidInputException.class)
    public void testArrivalMinutesAreNull() throws InvalidInputException {
        Timetable t = uiValidator.validateTime(depDate, arrDate, "10", "12", "00", null);
    }

    @Test (expected = InvalidInputException.class)
    public void testArrivalBeforeDeparture() throws InvalidInputException {
        arrDate = LocalDate.of(2018, 7, 10);
        Timetable t = uiValidator.validateTime(depDate, arrDate, "10", "12", "00", "00");
    }

    @Test
    public void testValidTimeEntered() throws InvalidInputException {
        Timetable t = uiValidator.validateTime(depDate, arrDate, "10", "12", "00", "00");
        Assert.assertNotNull(t);
    }

    @Test (expected = InvalidInputException.class)
    public void testEmptyPrice() throws InvalidInputException {
        double price = uiValidator.validatePrice("");
    }

    @Test (expected = InvalidInputException.class)
    public void testNullPrice() throws InvalidInputException {
        double price = uiValidator.validatePrice(null);
    }

    @Test (expected = InvalidInputException.class)
    public void testTextPrice() throws InvalidInputException {
        double price = uiValidator.validatePrice("price");
    }

    @Test
    public void testValidPrice() throws InvalidInputException {
        double price = uiValidator.validatePrice("10");
        Assert.assertEquals(price, 10.0, 0);
    }

    @Test (expected = CustomerException.class)
    public void testNoName() throws CustomerException {
        uiValidator.validateCustomer(null, "Mustermann", "0000010", "max@haha.com", LocalDate.of(2003, 05, 05));
    }

    @Test (expected = CustomerException.class)
    public void testNoLastName() throws CustomerException {
        uiValidator.validateCustomer("Max", null, "0000010", "max@haha.com", LocalDate.of(2003, 05, 05));
    }

    @Test (expected = CustomerException.class)
    public void testNoSVNR() throws CustomerException {
        uiValidator.validateCustomer("Max", "Muster", "max@haha.com", null , LocalDate.of(2003, 05, 05));
    }

    @Test (expected = CustomerException.class)
    public void testInvalidSVNR() throws CustomerException {
        uiValidator.validateCustomer("Max", "Muster", "max@haha.com", "kkkk0 000000", LocalDate.of(2003, 05, 05));
    }

    @Test (expected = CustomerException.class)
    public void testNoEmail() throws CustomerException {
        uiValidator.validateCustomer("Max", "Muster", null, "00000", LocalDate.of(2003, 05, 05));
    }

    @Test (expected = CustomerException.class)
    public void testPersonTooYoung() throws CustomerException {
        uiValidator.validateCustomer("Max", "Muster", "max@muster.com", "00000", LocalDate.of(2006, 05, 05));
    }

    @Test (expected = CustomerException.class)
    public void testPersonBornInFuture() throws CustomerException {
        uiValidator.validateCustomer("Max", "Muster", "max@muster.com", "00000", LocalDate.of(2019, 05, 05));
    }

    @Test (expected = CustomerException.class)
    public void testbirthdayIsNull() throws CustomerException {
        uiValidator.validateCustomer("Max", "Muster", "max@muster.com", "00000", null);
    }

    @Test
    public void testValidCustomer() throws CustomerException {
        uiValidator.validateCustomer("Max", "Muster", "max@muster.com", "00000", LocalDate.of(2003, 05, 05));
    }




}
