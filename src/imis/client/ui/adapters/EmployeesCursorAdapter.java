package imis.client.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.ui.ColorUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 23:05
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
        String time = (employee.getLastEventTime() != null) ? AppUtil.formatEmpDate(employee.getLastEventTime()) : "";
        ((TextView) view.findViewById(R.id.emp_time)).setText(time);
        view.findViewById(R.id.emp_kod_po).setBackgroundColor(ColorUtil.getColor(employee.getKod_po()));
    }
}
