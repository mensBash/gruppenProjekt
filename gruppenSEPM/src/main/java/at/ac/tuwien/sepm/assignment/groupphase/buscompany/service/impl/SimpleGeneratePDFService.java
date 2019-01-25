package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Booking;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.BookingEntry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.GeneratePDFService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PrintFailedException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Service
public class SimpleGeneratePDFService implements GeneratePDFService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private String ticketInfo;
    private String path;

    public SimpleGeneratePDFService() {
        try {
            Files.createDirectory(Paths.get(System.getProperty("user.home") + "/busFirmaTickets"));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    public void generatePDF(Booking booking) throws ServiceException {

        List<BookingEntry> discover = new LinkedList<>();

        for (BookingEntry b :
            booking.getEntries()) {

            List<BookingEntry> list = new LinkedList<>();
            list.add(b);
            boolean premium = false;

            for (BookingEntry bE :
                booking.getEntries()) {
                if (b.getCustomer().equals(bE.getCustomer()) && !b.equals(bE)) {
                    list.add(bE);
                    discover.add(bE);
                    if (b.getCustomer().getSocialNumber() != null) {
                        premium = true;
                    }
                }
            }

            if (!list.isEmpty())
                list.sort((o1, o2) -> {
                    int x = 0;
                    if (o1.getRoute().getTimetable().getDepartureTime().isBefore(o2.getRoute().getTimetable().getDepartureTime())) {
                        x = -9;
                    } else if (o1.getRoute().getTimetable().getDepartureTime().isAfter(o2.getRoute().getTimetable().getDepartureTime())) {
                        x = 9;
                    }
                    return x;
                });


            double prise = 0;

            for (BookingEntry bE :
                list) {
                if (premium) {
                    prise += bE.getRoute().getPrice() - bE.getRoute().getPrice() * 0.1;
                } else {
                    prise += bE.getRoute().getPrice();
                }
            }


            String first = "Booking number: " + booking.getBookingNr() + "\nFirst name: " + b.getCustomer().getFirstName() + "" +
                "\tLast name: " + b.getCustomer().getLastName();


            String last = "\nTotal Price: " + prise + " EUR";

            StringBuilder routeString = new StringBuilder("\n\nOutward:");

            City city = null;
            LocalDateTime localDateTime = null;

            for (BookingEntry bE : list) {

                if (city != null && localDateTime != null
                    && localDateTime.plusHours(5).plusMinutes(1).isBefore(bE.getRoute().getTimetable().getDepartureTime())
                    && city.equals(bE.getRoute().getStartDestination())) {
                    routeString.append("\nReturn:");
                }
                routeString.append("\nStart destination: ").append(bE.getRoute().getStartDestination().getName()).append(", ").append(bE.getRoute().getStartDestination().getCountry())
                    .append("\nBus number: ").append(bE.getRoute().getBusNumber())
                    .append("\nDeparture:  ").append(bE.getRoute().getTimetable().getDepartureTime().toLocalDate()).append("  ").append(bE.getRoute().getTimetable().getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .append("\nArrival:  ").append(bE.getRoute().getTimetable().getArrivalTime().toLocalDate()).append("  ").append(bE.getRoute().getTimetable().getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .append("\nEnd destination: ").append(bE.getRoute().getEndDestination().getName()).append(", ").append(bE.getRoute().getEndDestination().getCountry()).append("\n");

                city = bE.getRoute().getEndDestination();
                localDateTime = bE.getRoute().getTimetable().getArrivalTime();


            }

            ticketInfo = first + "\n";
            ticketInfo += routeString + "\n";
            ticketInfo += last + "\n";


            if (!discover.contains(b)) {
                generateQR(b);
                try {

                    LOG.debug("generating PDF of " + b.getCustomer().toString() + " service started!");
                    path = System.getProperty("user.home") + "/busFirmaTickets/" + b.getCustomer().getFirstName() + "_" +
                        b.getCustomer().getLastName() + "_" + LocalDateTime.now().toLocalDate().hashCode() + "_" + LocalDateTime.now().toLocalTime().hashCode() + ".pdf";
                    //initialize pdf writer
                    FileOutputStream fileOutputStream = new FileOutputStream(path);
                    PdfWriter pdfWriter = new PdfWriter(fileOutputStream);
                    LOG.debug("path of saving: " + fileOutputStream.toString());

                    //initialize pdf Doc
                    PdfDocument pdf = new PdfDocument(pdfWriter);
                    pdf.setDefaultPageSize(PageSize.A4);
                    //initialize layout Doc
                    Document document = new Document(pdf);

                    //add image to Doc
                    ImageData data = ImageDataFactory.create("src/main/resources/QR.png");
                    Image img = new Image(data);
                    img.setMargins(25f, 25f, 25f, 25f);
                    img.setTextAlignment(TextAlignment.RIGHT);
                    img.setMarginRight(0.5f);
                    img.setMarginTop(0.5f);
                    img.setHeight(128.0f);
                    img.setWidth(128.0f);
//                  img.setAutoScale(true);
                    document.add(img);

                    //"--------------------------------------------------------------------------\n" +
                    //+ "\n--------------------------------------------------------------------------"

                    //document.add(p);
                    Paragraph p = new Paragraph(
                        first + routeString.toString() + last);
                    p.setFontSize(10.9f);
                    p.setTextAlignment(TextAlignment.CENTER);
                    document.add(p);


                    document.close();
                    pdf.close();
                    pdfWriter.close();
                    fileOutputStream.close();

                } catch (IOException e) {
                    LOG.debug("generating PDF service failed!");
                    throw new ServiceException("pdf file could not be generated!", e);
                } finally {
                    if (new File("src/main/resources/QR.png").delete()) {
                        LOG.debug("QR image deleted!");
                    }
                }

                LOG.debug("generating PDF of " + b.getCustomer().toString() + " service succeeded!");
            }
        }

    }

    public void generateQR(BookingEntry bookingEntry) throws ServiceException {
        LOG.debug("generating QR service started!");

        ByteArrayOutputStream byteArrayOutputStream;
        FileOutputStream fileOutputStream;
        byteArrayOutputStream = QRCode.from(ticketInfo).withSize(340, 340).to(ImageType.PNG).stream();
        try {
            fileOutputStream = new FileOutputStream("src/main/resources/QR.png");
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();

            fileOutputStream.close();
            byteArrayOutputStream.close();

        } catch (IOException e) {
            LOG.debug("generating QR service failed!");
            throw new ServiceException("QR image could not be generated!", e);
        }

        LOG.debug("generating QR service succeeded!");

    }

    @Override
    public void printTicket() throws PrintFailedException {
        try {
            PDDocument pdf;
            pdf = PDDocument.load(new File(path));
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(pdf));
            if (job.printDialog()) {
                job.print();
            } else {
                throw new PrintFailedException("Ticket must be printed in order to complete booking.");
            }
        } catch (IOException | PrinterException e) {
            throw new PrintFailedException("Ticket must be printed in order to complete booking.",e);
        }
    }

}
