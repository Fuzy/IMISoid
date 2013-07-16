package imis.client.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.TextView;
import imis.client.model.Employee;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 8.5.13
 * Time: 17:09
 */
public class EmployeeResourceCursorAdapter extends ResourceCursorAdapter {

    public EmployeeResourceCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Employee employee = Employee.cursorToEmployee(cursor);
        String name = employee.getName();
        String kodpra = employee.getKodpra();
        StringBuilder builder = new StringBuilder();
        if (name != null) builder.append(name);
        if (kodpra != null) builder.append("(" + kodpra + ")");
        if (name == null && kodpra == null) builder.append(employee.getIcp());
        ((TextView) view.findViewById(android.R.id.text1)).setText(builder.toString());
    }
}
