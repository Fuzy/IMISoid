package imis.client.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;
import imis.client.ui.ColorConfig;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 2.5.13
 * Time: 21:23
 */
public class EmployeeWidgetProvider extends AppWidgetProvider {
    private static final String TAG = EmployeeWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d(TAG, "onUpdate() appWidgetIds " + Arrays.toString(appWidgetIds));
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "updateAppWidget() appWidgetId " + appWidgetId);

        Employee employee = EmployeeManager.getEmployeeOnWidgetId(context, appWidgetId);
        if (employee != null) {
            Log.d(TAG, "updateAppWidget() employee " + employee);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.employee_preview);
            String name = (employee.getKodpra() != null) ? employee.getKodpra() : employee.getIcp();
            views.setTextViewText(R.id.emp_kodpra, name);
            String last = employee.getDruh() + " " + TimeUtil.formatEmpDate(employee.getDatum())
                    + " " + TimeUtil.formatTimeInNonLimitHour(employee.getCas());
            views.setTextViewText(R.id.emp_time, last);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            //context.getSharedPreferences(AppConsts.PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
            int color = settings.getInt(employee.getKod_po(), ColorConfig.getColor(context, employee.getKod_po()));
            views.setInt(R.id.emp_kod_po, "setBackgroundColor", color);

            // Tell the widget manager
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted() appWidgetIds " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            EmployeeManager.resetEmployeeWidgetId(context, appWidgetId);
        }
    }

    public void updateAllWidgets(Context context) {
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(
                new ComponentName(context, EmployeeWidgetProvider.class));
        Log.d(TAG, "updateAllWidgets() ids " + Arrays.toString(ids));
        for (int id : ids) {
            updateAppWidget(context, man, id);
        }
    }

}
