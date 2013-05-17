package imis.client.asynctasks.result;

import org.springframework.http.HttpStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 17.5.13
 * Time: 22:25
 */
public class Result {
    protected HttpStatus statusCode;
    protected String msg;
    protected boolean err;

    public Result(HttpStatus statusCode) {
        this.statusCode = statusCode;
        this.err = false;
    }

    public Result(HttpStatus statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.err = true;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isErr() {
        return err;
    }
}
