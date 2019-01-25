package ServiceTest;

import at.ac.tuwien.sepm.assignment.groupphase.exception.PaymentValidatorException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.MakingPaymentService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl.SimpleMakingPaymentService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MakingPaymentServiceTest {

    @InjectMocks
    private MakingPaymentService service = new SimpleMakingPaymentService();


    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void addingWrongIBAN() throws ServiceException, PaymentValidatorException {

        thrown.expect(PaymentValidatorException.class);
        thrown.reportMissingExceptionWithMessage("PaymentValidatorException expected");
        service.checkPayment("DE02120300000000202052");

    }

    @Test
    public void addingWrongCreditCard() throws ServiceException, PaymentValidatorException {
        thrown.expect(PaymentValidatorException.class);
        thrown.reportMissingExceptionWithMessage("PaymentValidatorException expected");
        service.checkPayment("4929973256608984");

    }

    @Test
    public void addingValidINFO() throws ServiceException, PaymentValidatorException {
        service.checkPayment("4929973256608983");

    }

    @Test
    public void addingValidINFO2() throws ServiceException, PaymentValidatorException {
        service.checkPayment("DE02120300000000202051");
    }


    @Test
    public void addingInvalidInput() throws ServiceException, PaymentValidatorException {
        thrown.expect(PaymentValidatorException.class);
        thrown.reportMissingExceptionWithMessage("PaymentValidator expected");
        service.checkPayment("fd50");
    }



}
