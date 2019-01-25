package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service;

import at.ac.tuwien.sepm.assignment.groupphase.exception.PrintFailedException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Booking;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.BookingEntry;
import org.springframework.stereotype.Service;

@Service
public interface GeneratePDFService {

    /**
     * this method generate a pdf file and save it in resources, which include all information about the customer ticket.
     *
     * @throws ServiceException when any error exist.
     */
    void generatePDF(Booking booking) throws ServiceException;

    /**
     * this method generate a QR Image with type png and save it in resources, which include the critical information about the customer ticket.
     *
     * @throws ServiceException when any error exist.
     */
    void generateQR(BookingEntry booking) throws ServiceException;

    /**
     * Opens print dialog in order to finish printing ticket
     * @throws PrintFailedException - when there is no valid printer, i.e. when the ticket could not be printed for some reason
     */
    void printTicket() throws PrintFailedException;

}
