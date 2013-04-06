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
import imis.client.model.Employee;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 23:05
 */
public class EmployeesAdapter extends CursorAdapter {
    private static final String TAG = EmployeesAdapter.class.getSimpleName();

    public EmployeesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "newView()");
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View employeeView = inflater.inflate(R.layout.employee_profile, null);
        TextView textView = (TextView) employeeView.findViewById(R.id.grid_item_label);
        Employee employee = Employee.cursorToEmployee(cursor);
        String name = (employee.getKodpra() != null) ? employee.getKodpra() : employee.getIcp();
        textView.setText(name);
        return employeeView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
