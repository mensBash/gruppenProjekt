package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddRouteController extends AddOrModifyController {
    @Autowired
    public AddRouteController(RouteService routeService, BookingService bookingService) {
        super(routeService, bookingService);
    }
}
