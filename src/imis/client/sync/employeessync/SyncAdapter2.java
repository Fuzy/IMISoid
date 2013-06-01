package imis.client.sync.employeessync;

import android.accounts.Account;
import android.appwidget.AppWidgetManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import imis.client.asynctasks.result.ResultItem;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;
import imis.client.widget.WidgetProvider;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 24.5.13
 * Time: 21:09
 */
public class SyncAdapter2 extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter2.class.getSimpleName();
    private final Context context;

    public SyncAdapter2(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync()");

        EmployeesSync sync = new EmployeesSync(context);

        List<Employee> employees = EmployeeManager.getEmployeesWithWidget(context);
        Log.d(TAG, "onPerformSync() employees " + employees);
        ResultItem<Employee> employeeToSync;
        for (Employee employee : employees) {
            employeeToSync = sync.getEmployeeLastEvent(employee.getIcp());
            if (employeeToSync.isEmpty()) {
                Log.d(TAG, "onPerformSync() isEmpty");
            } else {
                Log.d(TAG, "onPerformSync()  updating widget Icp() " + employee.getIcp());
                EmployeeManager.updateEmployeeOnIcp(context, employeeToSync.getItem());
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                WidgetProvider.updateAppWidget(context, appWidgetManager, employee.getWidgetId());
                //TODO lze vyvolat aktualizaci vsech widgetu daneho typu
            }

        }
    }
}
