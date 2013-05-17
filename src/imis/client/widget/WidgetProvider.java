package imis.client.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;
import imis.client.ui.ColorUtil;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 2.5.13
 * Time: 21:23
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate()");
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        Log.d(TAG, "updateAppWidget() appWidgetId " + appWidgetId);

        // refresh widget
        Employee employee = EmployeeManager.getEmployee(context, appWidgetId);
        if (employee != null) {
            Log.d(TAG, "updateAppWidget() employee " + employee);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.employee_preview);
            String name = (employee.getKodpra() != null) ? employee.getKodpra() : employee.getIcp();
            views.setTextViewText(R.id.emp_kodpra, name);
            Long time = employee.getLastEventTime();
            if (time != null) views.setTextViewText(R.id.emp_time, AppUtil.formatEmpDate(time));
            views.setInt(R.id.emp_kod_po, "setBackgroundColor", ColorUtil.getColor(employee.getKod_po()));

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
}
