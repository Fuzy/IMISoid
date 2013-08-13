package imis.client.exceptions;

/**
 * Exception is thrown if user is not selected.
 */
public class NotUserSelectedException extends Exception {

    public NotUserSelectedException(String detailMessage) {
        super(detailMessage);
    }
}
