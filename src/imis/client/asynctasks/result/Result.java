package imis.client.asynctasks.result;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 17.5.13
 * Time: 22:25
 */
public class Result {
    protected HttpStatus statusCode;
    protected String msg;
    protected boolean unknownErr;
    protected Map<String, Object> statistics;

    public Result() {
        this.unknownErr = true;
        this.msg = "";
    }

    public Result(String msg) {
        this.unknownErr = true;
        this.msg = msg;
    }

    public Result(HttpStatus statusCode) {
        this.statusCode = statusCode;
        this.msg = "";
        this.unknownErr = false;
    }

    public Result(HttpStatus statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.unknownErr = false;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }

    public boolean isUnknownErr() {
        return unknownErr;
    }

    public boolean isServerError() {
        return (statusCode != null && statusCode.value() >= 500 && statusCode.value() < 600);
    }

    public boolean isClientError() {
        return (statusCode != null && statusCode.value() >= 400 && statusCode.value() < 500);
    }

    public boolean isOk() {
        return (!isUnknownErr() && !isServerError() && !isClientError());
    }
/*
    @Override
    public String toString() {
        return "Result{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", unknownErr=" + unknownErr +
                ", statistics=" + ((statistics == null) ? null : statistics.keySet()) + '}';
    }*/
}
