package imis.client.sync.employeessync;

import android.accounts.Account;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultItem;
import imis.client.model.Employee;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EmployeeManager;
import imis.client.widget.EmployeeWidgetProvider;
import org.apache.http.HttpStatus;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 24.5.13
 * Time: 21:09
 */
public class SyncAdapterEmployeeWidgets extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapterEmployeeWidgets.class.getSimpleName();
    private final Context context;

    public SyncAdapterEmployeeWidgets(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync()" + "account = [" + account + "], extras = [" + extras + "], " +
                "authority = [" + authority + "], provider = [" + provider + "], syncResult = [" + syncResult + "]");

        EmployeesSync sync = new EmployeesSync(context);

        List<Employee> employees = EmployeeManager.getEmployeesWithWidget(context);
        if (employees.size() > 0) {

            Result testResult = NetworkUtilities.testWebServiceAndDBAvailability(context);
            if (testResult.getStatusCode() == null || testResult.getStatusCode().value() != HttpStatus.SC_OK) {
                Log.d(TAG, "onPerformSync() connection unavailable");
                syncResult.delayUntil = (System.currentTimeMillis() + AppConsts.MS_IN_MIN) / 1000L;
                ContentResolver.requestSync(account, authority, extras);
                return;
            }

            Log.d(TAG, "onPerformSync() employees " + employees);
            ResultItem<Employee> employeeToSync;
            for (Employee employee : employees) {
                employeeToSync = sync.getEmployeeLastEvent(employee.getIcp());
                if (employeeToSync.isEmpty()) {
                    Log.d(TAG, "onPerformSync() isEmpty");
                } else {
                    Log.d(TAG, "onPerformSync()  updating widget Icp() " + employee.getIcp());
                    EmployeeManager.updateEmployeeOnIcp(provider, employeeToSync.getItem());
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    new EmployeeWidgetProvider().updateAppWidget(context, appWidgetManager, employee.getWidgetId());
                }
            }
        }
    }


}
