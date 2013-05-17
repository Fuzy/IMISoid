package imis.client.asynctasks.result;

import org.springframework.http.HttpStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 27.4.13
 * Time: 19:37
 */
public class ResultData<T> extends Result {
    protected T[] array;

    public ResultData(HttpStatus statusCode, T[] array) {
        super(statusCode);
        this.array = array;
    }

    public ResultData(HttpStatus statusCode, String msg) {
        super(statusCode, msg);
    }

    public T[] getArray() {
        return array;
    }


}
