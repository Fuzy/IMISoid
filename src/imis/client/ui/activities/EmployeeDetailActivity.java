package imis.client.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.5.13
 * Time: 14:56
 */
public class EmployeeDetailActivity extends Activity {
    private static final String TAG = EmployeeDetailActivity.class.getSimpleName();
    private Employee employee;
    private ImageButton favButton;
    private String[] kody_po_values, kody_po_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_profile);

        kody_po_values = getResources().getStringArray(R.array.kody_po_values);
        kody_po_desc = getResources().getStringArray(R.array.kody_po_desc);

        long id = getIntent().getLongExtra(Employee.COL_ID, -1);
        employee = EmployeeManager.getEmployeeOnId(this, id);
        populateEmployeeFields();
    }

    private void populateEmployeeFields() {
        String name = employee.getName() + "(" + employee.getKodpra() + ")";
        ((TextView) findViewById(R.id.emp_name)).setText(name);
        int i = Arrays.asList(kody_po_values).indexOf(employee.getKod_po());
        if (i != -1) {
            ((TextView) findViewById(R.id.emp_kod_po)).setText(kody_po_desc[i]);
        }
        ((TextView) findViewById(R.id.emp_time)).setText(TimeUtil.formatTimeInNonLimitHour(employee.getCas()));

        favButton = (ImageButton) findViewById(R.id.emp_favorite);
        if (employee.isFav()) {
            setImageIsFavorite();
        } else {
            setImageIsNotFavorite();
        }

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIsFavorite();
            }
        });
    }

    private void changeIsFavorite() {
        if (employee.isFav()) {
            setImageIsNotFavorite();
            employee.setFav(false);
        } else {
            setImageIsFavorite();
            employee.setFav(true);
        }
    }

    private void setImageIsFavorite() {
        favButton.setImageResource(R.drawable.btn_rating_star_on_pressed);
    }

    private void setImageIsNotFavorite() {
        favButton.setImageResource(R.drawable.btn_rating_star_off_normal);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EmployeeManager.updateEmployeeIsFav(this, employee.get_id(), employee.isFav());
    }
}
