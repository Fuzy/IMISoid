package imis.client.sync.employeessync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;

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

        List<Employee> employees = EmployeeManager.getEmployeesWithWidget(context);
        Log.d(TAG, "onPerformSync() employees " + employees);
        for (Employee employee : employees) {
            //TODO async dotaz
            //WidgetProvider.updateAppWidget(this, appWidgetManager, employee.getWidgetId());

        }
    }
}
