package imis.client.asynctasks.result;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

/**
 *   Class stores result of HTTP request which returns list of records.
 */
public class ResultList<T> extends Result {
    protected T[] array;

    public ResultList(HttpStatus statusCode, T[] array) {
        super(statusCode);
        this.array = array;
    }

    public ResultList(String msg) {
        super(msg);
    }

    public ResultList(HttpStatus statusCode, String msg) {
        super(statusCode, msg);
    }

    public T[] getArray() {
        return array;
    }

    public boolean isEmpty() {
        return array == null || array.length == 0;
    }

    @Override
    public String toString() {
        return "ResultList{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", unknownErr=" + unknownErr +
                ", array=" + Arrays.toString(array) +
                ", statistics=" + ((statistics == null) ? null : statistics.keySet()) + '}';
    }
}
