package at.ac.tuwien.sepm.assignment.groupphase.buscompany.service;


import at.ac.tuwien.sepm.assignment.groupphase.exception.PaymentValidatorException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;

public interface MakingPaymentService {


    /**
     * check if all inputs are valid and there is at least one method for paying.
     *
     * @param cardNumber - Iban or Credit Card Number to be verified
     * @throws ServiceException          - thrown when both param are empty.
     * @throws PaymentValidatorException - thrown when one param is not valid
     */
    void checkPayment(String cardNumber) throws ServiceException, PaymentValidatorException;


}
