package imis.client.asynctasks.result;

import org.springframework.http.HttpStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 1.6.13
 * Time: 16:51
 */
public class ResultItem<T> extends Result {
    protected T item;

    public ResultItem(HttpStatus statusCode, T item) {
        super(statusCode);
        this.item = item;
    }

    public ResultItem(String msg) {
        super(msg);
    }

    public ResultItem(HttpStatus statusCode, String msg) {
        super(statusCode, msg);
    }

    public T getItem() {
        return item;
    }

    public boolean isEmpty() {
        return item == null;
    }

    @Override
    public String toString() {
        return "ResultItem{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", unknownErr=" + unknownErr +
                ", item=" + item +
                '}';
    }
}
