package imis.client.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;

import java.util.Map;

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
    private Map<String, String> codes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_profile);
        codes = AppUtil.getCodes(this);

        long id = getIntent().getLongExtra(Employee.COL_ID, -1);
        employee = EmployeeManager.getEmployeeOnId(this, id);
        populateEmployeeFields();
    }

    private void populateEmployeeFields() {
        String name = employee.getName() + "(" + employee.getKodpra() + ")";
        ((TextView) findViewById(R.id.emp_name)).setText(name);
        if (codes.containsKey(employee.getKod_po())) {
            ((TextView) findViewById(R.id.emp_kod_po)).setText(codes.get(employee.getKod_po()));
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
