package loanapp.loan;

import org.fabric3.samples.bigbank.api.loan.LoanException;

/**
 * @version $Revision: 8763 $ $Date: 2010-03-29 11:52:36 +0200 (Mon, 29 Mar 2010) $
 */
public class InvalidLoanData extends LoanException {
    private static final long serialVersionUID = -8593489214510658391L;

    public InvalidLoanData(String message) {
        super(message);
    }
}
