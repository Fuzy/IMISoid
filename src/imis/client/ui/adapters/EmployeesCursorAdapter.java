package imis.client.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.model.Employee;
import imis.client.ui.ColorConfig;


/**
 *  Adapter which makes accessible employees obtained from database.
 */
public class EmployeesCursorAdapter extends CursorAdapter {
    private static final String TAG = EmployeesCursorAdapter.class.getSimpleName();
    private LayoutInflater inflater;

    public EmployeesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "newView()");
        return inflater.inflate(R.layout.employee_preview, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Employee employee = Employee.cursorToEmployee(cursor);

        String name = (employee.getKodpra() != null) ? employee.getKodpra() : employee.getIcp();
        ((TextView) view.findViewById(R.id.emp_kodpra)).setText(name);
        StringBuilder last = new StringBuilder();
        if (employee.getDruh() != null) last.append(employee.getDruh() + " ");
        if (employee.getDatum() != null) last.append(TimeUtil.formatEmpDate(employee.getDatum()));
        if (employee.getCas() != null) last.append(TimeUtil.formatTimeInNonLimitHour(employee.getCas()));
        ((TextView) view.findViewById(R.id.emp_time)).setText(last.toString());
        view.findViewById(R.id.emp_kod_po).setBackgroundColor(ColorConfig.getColor(context, employee.getKod_po()));
    }
}
