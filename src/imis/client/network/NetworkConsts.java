package imis.client.network;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 19.6.13
 * Time: 19:31
 */
public class NetworkConsts {
    public static final String SCHEME = "http://";
    public static final String BASE_PATH = "/Imisoid_WS";
    private static final String EVENTS_PATH = "/events";
    public static final String EVENTS_CREATE_PATH = EVENTS_PATH;
    public static final String EVENTS_DELETE_PATH = EVENTS_PATH + "/{rowid}";
    public static final String EVENTS_UPDATE_PATH = EVENTS_PATH + "/{rowid}";
    public static final String EVENTS_GET_PATH = EVENTS_PATH + "/{icp}?from={from}&to={to}";
    public static final String EVENTS_TIME_GET_PATH = EVENTS_PATH + "/time/{icp}?from={from}&to={to}";
    private static final String RECORDS_PATH = "/records";
    public static final String RECORDS_GET_PATH = RECORDS_PATH + "/{kodpra}?from={from}&to={to}";
    public static final String RECORDS_TIME_GET_PATH = RECORDS_PATH + "/time/{icp}?from={from}&to={to}";
    private static final String EMPLOYEES_PATH = "/employees";
    public static final String EMPLOYEE_GET_PATH = EMPLOYEES_PATH + "/{icp}";
    public static final String EMPLOYEES_GET_PATH = EMPLOYEES_PATH + "/all/{icp}";
    public static final String EMPLOYEES_GET_EVENTS_PATH = EMPLOYEES_PATH + "/lastevents";
    public static final String EMPLOYEES_GET_EVENT_PATH = EMPLOYEES_PATH + "/lastevents/{icp}";
    public static final String TEST_PATH = "/test";
    public static final String TEST_MODE = "test";
    public static final String TEST_CONN = "/testconnection";
    public static final String DOMAIN_DEFAULT = "10.0.0.2";
    public static final int PORT_DEFAULT = 8081;
    public static final String BASE_URI_DEFAULT = SCHEME + DOMAIN_DEFAULT + ":" + PORT_DEFAULT + TEST_PATH;
}
