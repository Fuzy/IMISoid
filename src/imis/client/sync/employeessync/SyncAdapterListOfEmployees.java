package imis.client.sync.employeessync;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultList;
import imis.client.model.Employee;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EmployeeManager;
import org.apache.http.HttpStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 24.5.13
 * Time: 21:09
 */
public class SyncAdapterListOfEmployees extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapterListOfEmployees.class.getSimpleName();
    private final Context context;

    public SyncAdapterListOfEmployees(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync()" + "account = [" + account + "], extras = [" + extras + "], " +
                "authority = [" + authority + "], provider = [" + provider + "], syncResult = [" + syncResult + "]");

        Result testResult = NetworkUtilities.testWebServiceAndDBAvailability(context);
        if (testResult.getStatusCode() == null || testResult.getStatusCode().value() != HttpStatus.SC_OK) {
            Log.d(TAG, "onPerformSync() connection unavailable");
            syncResult.delayUntil = (System.currentTimeMillis() + AppConsts.MS_IN_MIN) / 1000L;
            ContentResolver.requestSync(account, authority, extras);
            return;
        }

        EmployeesSync sync = new EmployeesSync(context);
        ResultList<Employee> resultList = sync.getListOfEmployees();
        if (resultList.isOk() && !resultList.isEmpty()) {
            Log.d(TAG, "onPostExecute() OK and not empty");
            Employee[] employees = resultList.getArray();
            if (employees != null) {
                EmployeeManager.syncEmployees(context, provider, employees);//TODO test
            }
        }
    }

}
