package loanapp.acceptance;

import org.fabric3.samples.bigbank.api.loan.LoanException;

/**
 * @version $Revision$ $Date$
 */
public class LoanOfferExpiredException extends LoanException {
    private static final long serialVersionUID = -3376572554252218803L;

    public LoanOfferExpiredException(String message) {
        super(message);
    }
}
