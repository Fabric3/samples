package  org.fabric3.samples.bigbank.loan.acceptance;

import org.fabric3.samples.bigbank.api.loan.LoanException;

/**
 * @version $Revision: 8763 $ $Date: 2010-03-29 11:52:36 +0200 (Mon, 29 Mar 2010) $
 */
public class LoanOfferExpiredException extends LoanException {
    private static final long serialVersionUID = -3376572554252218803L;

    public LoanOfferExpiredException(String message) {
        super(message);
    }
}
