package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.impl;

import at.ac.tuwien.sepm.assignment.groupphase.exception.PaymentValidatorException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.MakingPaymentService;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.CreditCardValidator;

import java.lang.invoke.MethodHandles;


@Service
public class SimpleMakingPaymentService implements MakingPaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void checkPayment(String cardNumber) throws ServiceException, PaymentValidatorException {
        LOG.debug("MakingPayment service check activated!");

        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new ServiceException("Please provide IBAN or Credit Card Number.");
        }
        IBANCheckDigit a = new IBANCheckDigit();
        CreditCardValidator a2 = new CreditCardValidator();
        boolean x = a.isValid(cardNumber);
        boolean f = a2.isValid(cardNumber);
        if (!(x || f)) {
            throw new PaymentValidatorException("IBAN or Credit card number is invalid. Please recheck your entry");
        }

        LOG.debug("MakingPayment service check success!");
    }

}
