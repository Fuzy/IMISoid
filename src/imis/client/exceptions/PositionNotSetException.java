package imis.client.exceptions;

/**
 * Exception is thrown when position of user workplace is not set.
 */
public class PositionNotSetException extends Exception {

    public PositionNotSetException(String detailMessage) {
        super(detailMessage);
    }
}
