package loanapp.store;

/**
 * @version $Revision: 8744 $ $Date: 2010-03-25 19:43:45 +0100 (Thu, 25 Mar 2010) $
 */
public class StoreException extends Exception {
    private static final long serialVersionUID = 5946877131537880818L;

    public StoreException(Throwable cause) {
        super(cause);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreException(String message) {
        super(message);
    }
}
